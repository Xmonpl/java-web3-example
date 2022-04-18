package org.eu.xmon.web3example.config;

import lombok.Getter;

import java.nio.file.Path;

public class Settings extends xmonConfiguration<Settings> {
    @Getter
    private final String jdbcURL;
    @Getter
    private final String username;
    @Getter
    private final String password;

    public Settings(){
        super("settings.json", Path.of("."), Settings.class);
        this.jdbcURL = "jdbc:mariadb://ip:port/dbname";
        this.username = "username";
        this.password = "password";
    }
}
