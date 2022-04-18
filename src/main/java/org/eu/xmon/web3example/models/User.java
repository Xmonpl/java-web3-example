package org.eu.xmon.web3example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Table(name = "users")
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id")
    public Integer id;

    @Column(name = "nonce")
    public Integer nonce;

    @Column(name = "publicAddress")
    public String publicAddress;

    @Column(name = "username")
    public String username;
}
