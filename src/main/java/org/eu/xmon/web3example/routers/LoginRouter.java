package org.eu.xmon.web3example.routers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ftpix.sparknnotation.annotations.SparkController;
import com.ftpix.sparknnotation.annotations.SparkPost;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eu.xmon.web3example.App;
import org.eu.xmon.web3example.models.User;
import org.eu.xmon.web3example.utils.EthersUtils;
import spark.Request;
import spark.Response;

@SparkController
public class LoginRouter {
    private static final String secret = "secret";
    @SparkPost("/login")
    public String login(final Request req, final Response res) {
        JsonObject jsonObject = new Gson().fromJson(req.queryParams().toArray()[0].toString(), JsonObject.class);
        final String request = jsonObject.get("request").getAsString();
        String address = jsonObject.get("address").getAsString();
        if (request == null || address == null){
            return "Error : request or address is null";
        }
        address = address.toLowerCase();

        if (request.equals("login")) {
            Integer nonce =  App.database.sql("SELECT nonce from users WHERE publicAddress = ?", address).first(Integer.class);
            if (nonce == null) {
                nonce = (int) (Math.random() * 10000);
                final User user = User.builder()
                        .nonce(nonce)
                        .publicAddress(address)
                        .build();
                if (App.database.insert(user).execute().getRowsAffected() == 1){
                    return "Sign this message to validate that you are the owner of the account. Random string: " + nonce;
                }else{
                    return "Error : can't create user";
                }
            }else{
                return "Sign this message to validate that you are the owner of the account. Random string: " + nonce;
            }
        } else if (request.equals("auth")) {
            final String signature = jsonObject.get("signature").getAsString();
            Integer nonce =  App.database.sql("SELECT nonce from users WHERE publicAddress = ?", address).first(Integer.class);
            final String message = "Sign this message to validate that you are the owner of the account. Random string: " + nonce;
            final String verified = EthersUtils.verifyMessage(message, signature);
            if (address.equals(verified)){
                final User user = App.database.sql("SELECT * from users WHERE publicAddress = ?", address).first(User.class);
                if (user != null) {
                    App.database.sql("UPDATE users SET nonce = ? WHERE publicAddress = ?", (int) (Math.random() * 10000), address).execute();
                    final String token = JWT.create()
                            .withClaim("address", address)
                            .sign(Algorithm.HMAC256(secret));
                    final JsonArray jsonObject1 = new JsonArray();
                    jsonObject1.add("Success");
                    jsonObject1.add(user.username);
                    jsonObject1.add(token);
                    return jsonObject1.toString();
                }else{
                    return "Error : can't find user";
                }
            }else{
                return "Error : can't verify signature";
            }
        } else if (request.equals("updatePublicName")) {
            final String publicName = jsonObject.get("publicName").getAsString();
            final String jwt = jsonObject.get("JWT").getAsString();
            Algorithm algorithm = Algorithm.HMAC256(secret);
            try {
                JWTVerifier verifier = JWT.require(algorithm)
                        .withClaim("address", address)
                        .build();
                verifier.verify(jwt);
                if (App.database.sql("UPDATE users SET username = ? WHERE publicAddress = ?", publicName, address).execute().getRowsAffected() == 1){
                    return "Public name for " + address + " updated to " + publicName;
                }else{
                    return "Error : can't update public name";
                }
            }catch (JWTVerificationException exception){
                return "Error : can't verify signature";
            }
        }
        return "";
    }
}
