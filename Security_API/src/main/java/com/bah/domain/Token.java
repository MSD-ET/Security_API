package com.bah.domain;

public class Token {
	@Override
	public String toString() {
		return "Token [token=" + token + "]";
	}

	String token;

	public Token(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
