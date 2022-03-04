package com.zc.JWT;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.zc.credentialsVault.JWTCredentialsVault;
import com.zc.loginservlet.UserDetailClass;

public class JsonWebToken {

	// Non MFA USER ID Token Generator method
	public JSONObject NonMfaUserIDToken(String useremail, int mfaEnrollmentStatus) {

		try {
			JWTCredentialsVault jwtCV = new JWTCredentialsVault();
			final String secret = jwtCV.getSecret();
			Algorithm algorithm = Algorithm.HMAC512(secret);
			long iat = new Date().getTime();
			long exp = iat + 60 * 60000;

			Date iatDate = new Date(iat);
			Date expDate = new Date(exp);

			Map<String, Object> payloadClaims = new HashMap<>();
			payloadClaims.put("email_verified", true);
			payloadClaims.put("email", useremail);

			if (mfaEnrollmentStatus == 0) {
				payloadClaims.put("mfa_status", "mfa not enrolled");
				payloadClaims.put("mfa_verified", "mfa not enrolled");
				payloadClaims.put("auth_status", "fully authenticated");
			}

			Map<String, Object> mfaInfo = new HashMap<String, Object>();

			if (mfaEnrollmentStatus == 0)
				mfaInfo.put("mfaEnrollemntId", mfaEnrollmentStatus);

			payloadClaims.put("mfaInfo", mfaInfo);

			String idToken = JWT.create()
					.withIssuer("localhost")
					.withPayload(payloadClaims)
					.withNotBefore(iatDate)
					.withExpiresAt(expDate)
					.withIssuedAt(iatDate)
					.withJWTId(UUID.randomUUID().toString())
					.sign(algorithm);

			return new JSONObject().put("idToken", idToken);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server problem, Try after sometimes");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server problem, Try after sometimes");
		}
	}

	public JSONObject mfaPendingCredToken(String useremail, int mfaEnrollmentStatus) {

		try {
			JWTCredentialsVault jwtCV = new JWTCredentialsVault();
			final String secret = jwtCV.getSecret();
			Algorithm algorithm = Algorithm.HMAC512(secret);
			long iat = new Date().getTime();
			long exp = iat + 60 * 60000;

			Date iatDate = new Date(iat);
			Date expDate = new Date(exp);

			Map<String, Object> payloadClaims = new HashMap<>();
			payloadClaims.put("email_verified", true);
			payloadClaims.put("email", useremail);

			Map<String, Object> mfaInfo = new HashMap<String, Object>();

			if (mfaEnrollmentStatus == 1) {
				UserDetailClass udc = new UserDetailClass();
				JSONObject userMfaId = udc.GetUserMFAEnrollmentID(useremail);

				if (userMfaId.has("error"))
					return userMfaId;

				mfaInfo.put("mfaEnrollemntId", (int) userMfaId.get("userMfaId"));
				ArrayList<String> useremails = udc.GetUserEmails(useremail);
				if (useremails.isEmpty())
					return new JSONObject().put("error", "Internal Server problem");

				mfaInfo.put("emailOptions", (List<String>) useremails);
				mfaInfo.put("displayName", "email");
			} else
				return new JSONObject().put("error", "Invalid request call to perform operation");

			payloadClaims.put("mfaInfo", mfaInfo);

			payloadClaims.put("mfa_status", "mfa enrolled");
			payloadClaims.put("mfa_verified", "mfa not verified");
			payloadClaims.put("auth_status", "mfa not verified");

			String mfaPendingCredToken = JWT.create()
					.withIssuer("localhost")
					.withPayload(payloadClaims)
					.withNotBefore(iatDate)
					.withExpiresAt(expDate)
					.withIssuedAt(iatDate)
					.withJWTId(UUID.randomUUID().toString())
					.sign(algorithm);

			JSONObject responseObj = new JSONObject();
			responseObj.put("mfaInfo", mfaInfo);
			responseObj.put("mfaPendingCredentials", mfaPendingCredToken);

			return responseObj;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server problem, Try after sometimes");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Internal Server Error");
		}
	}

	public JSONObject MfaEnrolledUserIDToken(String useremail) throws UnsupportedEncodingException {

		try {
			JWTCredentialsVault jwtCV = new JWTCredentialsVault();
			final String secret = jwtCV.getSecret();
			Algorithm algorithm = Algorithm.HMAC512(secret);
			long iat = new Date().getTime();
			long exp = iat + 60 * 60000;

			Date iatDate = new Date(iat);
			Date expDate = new Date(exp);

			Map<String, Object> payloadClaims = new HashMap<>();
			payloadClaims.put("email_verified", true);
			payloadClaims.put("email", useremail);

			payloadClaims.put("mfa_status", "mfa enrolled");
			payloadClaims.put("mfa_verified", "mfa verified successfully");
			payloadClaims.put("auth_status", "fully authenticated");
			
			Map<String, Object> mfaInfo = new HashMap<String, Object>();

			UserDetailClass udc = new UserDetailClass();
			JSONObject userMfaId = udc.GetUserMFAEnrollmentID(useremail);

			if (userMfaId.has("error"))
				return userMfaId;

			mfaInfo.put("mfaEnrollemntId", (int) userMfaId.get("userMfaId"));

			ArrayList<String> useremails = udc.GetUserEmails(useremail);
			if (useremails.isEmpty())
				return new JSONObject().put("error", "Internal Server problem");

			mfaInfo.put("emailOptions", (List<String>) useremails);
			mfaInfo.put("displayName", "email");
			
			payloadClaims.put("mfaInfo", mfaInfo);

			String idToken = JWT.create()
					.withIssuer("localhost")
					.withPayload(payloadClaims)
					.withNotBefore(iatDate)
					.withExpiresAt(expDate)
					.withIssuedAt(iatDate)
					.withJWTId(UUID.randomUUID().toString())
					.sign(algorithm);

			return new JSONObject().put("idToken", idToken);
		} catch (JWTCreationException exception) {
			exception.printStackTrace();
			return new JSONObject().put("error", "Server problem, Try after sometimes");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server problem, Try after sometimes");
		}
	}

	public JSONObject mfaPendingCredTokenDecoder(String token) {

		try {
			JWTCredentialsVault jwtCV = new JWTCredentialsVault();
			Algorithm algorithm = Algorithm.HMAC512(jwtCV.getSecret());
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("localhost").build(); // Reusable verifier instance
			DecodedJWT verifiedJWT = verifier.verify(token);
			if (verifiedJWT.getIssuer().equals("localhost")) {
				JSONObject claims = new JSONObject();
				claims.put("mfa_status", verifiedJWT.getClaim("mfa_status").asString());
				claims.put("auth_status", verifiedJWT.getClaim("auth_status").asString());
				claims.put("email", verifiedJWT.getClaim("email").asString());
				claims.put("email_verified", verifiedJWT.getClaim("email_verified").asBoolean());
				JSONObject mfaInfo = new JSONObject(verifiedJWT.getClaim("mfaInfo").toString());
				claims.put("mfaEnrollemntId", mfaInfo.get("mfaEnrollemntId"));
				claims.put("emailOptions", new JSONArray((JSONArray) mfaInfo.get("emailOptions")));
				return claims;
			}
			return new JSONObject().put("error", "Invalid Token");
		} catch (TokenExpiredException exp) {
			exp.printStackTrace();
			return new JSONObject().put("error", "Invalid Token, Token already expired");
		} catch (JWTVerificationException exception) {
			exception.printStackTrace();
			return new JSONObject().put("error", "Invalid Token");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Internal Server Problem");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Internal Server Problem");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server problem, Try after sometimes");
		}
	}

	public JSONObject JWTDecoder(String token) throws IllegalArgumentException, UnsupportedEncodingException {
		try {
			JWTCredentialsVault jwtCV = new JWTCredentialsVault();
			Algorithm algorithm = Algorithm.HMAC512(jwtCV.getSecret());
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("localhost").build(); // Reusable verifier instance

			DecodedJWT verifiedJWT = verifier.verify(token);

			// DecodedJWT decodedJWT = JWT.decode(token);

			if (verifiedJWT.getIssuer().equals("localhost")) {

				// JSONObject claims = new JSONObject(verifiedJWT.getClaim("mfaInfo").toString());
				JSONObject claims = new JSONObject();
				// claims.put("email_verified", verifiedJWT.getClaim("email_verified").asBoolean());
				// claims.put("iss", verifiedJWT.getClaim("iss").asString());
				// claims.put("exp", verifiedJWT.getClaim("exp").asLong());
				// claims.put("mfa_verfication", verifiedJWT.getClaim("mfa_verfication").asString());
				// claims.put("iat", verifiedJWT.getClaim("iat").asLong());
				// claims.put("email", verifiedJWT.getClaim("email").asString());

				claims.put("mfaInfo", verifiedJWT.getClaim("mfaInfo"));
				// System.out.println(new JSONObject(verifiedJWT.getClaim("mfaInfo")));
				// System.out.println(verifiedJWT.getClaims());
				// System.out.println(claims.get("emailOptions"));
				// JSONObject emailOptions = new JSONObject(claims.get("emailOptions"));
				// System.out.println(emailOptions);
				// System.out.println(claims);

				// System.out.println("Full mfaInfo");
				// System.out.println(claims.get("mfaInfo"));
				JSONObject j = new JSONObject(claims.get("mfaInfo").toString());
				System.out.println("Full mfaInfo \n" + j);

				System.out.println();
				System.out.println(j.get("emailOptions"));

				JSONArray ja = new JSONArray((JSONArray) j.get("emailOptions"));
				System.out.println("ja: " + ja);
				System.out.println(ja.get(0));

				System.out.println("All claims");
				System.out.println(verifiedJWT.getClaims());

				return claims;
			}
			return new JSONObject().put("error", "Invalid Token");
		} catch (TokenExpiredException exp) {
			exp.printStackTrace();
			return new JSONObject().put("error", "Invalid Token, Token already expired");
		} catch (JWTVerificationException exception) {
			exception.printStackTrace();
			return new JSONObject().put("error", "Invalid Token");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server problem, Try after sometimes");
		}
	}
}
