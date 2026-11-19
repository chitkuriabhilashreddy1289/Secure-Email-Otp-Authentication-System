package com.secureauth;
import jakarta.persistence.*;
@Entity
@Table(name="accounts")
public class Account {
 @Id public String email;
 @Column(nullable=false) public String name;
 public boolean verified;
 protected Account() {}
 Account(String email,String name){this.email=email;this.name=name;}
}
