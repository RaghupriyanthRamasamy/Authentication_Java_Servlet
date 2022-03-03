package com.zc.JWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.zc.credentialsVault.JWTCredentialsVault;

public class JsonWebToken {
	public JSONObject JWTPartialIDToken(String useremail) throws UnsupportedEncodingException {
		
		JWTCredentialsVault jwtCV = new JWTCredentialsVault(); 
		
		final String secret = jwtCV.getSecret();
		try {
		    Algorithm algorithm = Algorithm.HMAC512(secret);
		    long iat = new Date().getTime();
		    long exp = iat + 60*60000;
		    
		    Date iatDate = new Date(iat);
		    Date expDate = new Date(exp);

		    String idToken = JWT.create()
		        .withIssuer("localhost")
		        .withClaim("email_verified", true)
		        .withClaim("email", useremail)
		        .withClaim("iat", iat)
		        .withClaim("exp", exp)
		        .withClaim("mfa_verfication", "partially verified first factor")
		        .withExpiresAt(expDate)
		        .withIssuedAt(iatDate)
		        .sign(algorithm);
		    
		    JSONObject partialIdToken = new JSONObject();
		    
		    partialIdToken.put("idToken", idToken);
		    partialIdToken.put("expiresIn", String.valueOf(exp));
		    partialIdToken.put("mfaPendingCredential","Successfully passed first factor");
		    
		    JSONObject mfaInfo = new JSONObject();
		    mfaInfo.put("mfaEnrollemntId", "01");
		    mfaInfo.put("email", useremail);
		    
		    partialIdToken.put("mfaInfo", mfaInfo);
		    
		    return partialIdToken;
		} catch (JWTCreationException exception){
		    //Invalid Signing configuration / Couldn't convert Claims.
			return new JSONObject().put("error", "Server problem, Try after sometimes");
		}
	}
	
	public JSONObject IDToken(String useremail, int mfaEnrollmentStatus) {
		JWTCredentialsVault jwtCV = new JWTCredentialsVault(); 
		
		try {
			final String secret = jwtCV.getSecret();
			Algorithm algorithm = Algorithm.HMAC512(secret);
		    long iat = new Date().getTime();
		    long exp = iat + 60*60000;
		    
		    Date iatDate = new Date(iat);
		    Date expDate = new Date(exp);
		    
		    Map<String, Object> payloadClaims = new HashMap<>();
		    payloadClaims.put("email_verified", true);
		    payloadClaims.put("email", useremail);
		    payloadClaims.put("iat", iat);
		    payloadClaims.put("exp", exp);
		    
		    if (mfaEnrollmentStatus == 0) {
		    	payloadClaims.put("mfa_status", "mfa not enrolled");
		    	payloadClaims.put("mfa_verified", "mfa not enrolled");
		    	payloadClaims.put("auth_status", "fully authenticated");
			}
		    
		    if (mfaEnrollmentStatus == 1) {
		    	Map<String, Object> providerUserInfo = new HashMap<String, Object>();
			    providerUserInfo.put("federatedId", useremail);
			    providerUserInfo.put("email", useremail);
			    providerUserInfo.put("rawId", useremail);
			    payloadClaims.put("providerUserInfo", providerUserInfo);
			}
		    
		    Map<String, Object> mfaInfo = new HashMap<String, Object>();
		    
		    if (mfaEnrollmentStatus == 0)
		    	mfaInfo.put("mfaEnrollemntId", 0);
		    
		    if (mfaEnrollmentStatus == 1) {
		    	payloadClaims.put("mfa_status", "mfa enrolled");
		    	payloadClaims.put("mfa_verified", "mfa verified");
		    	mfaInfo.put("mfaEnrollemntId", 1);
			    mfaInfo.put("email", useremail);
			    mfaInfo.put("displayName", "email");
		    }
		    
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
		}
	}
	
	public JSONObject JWTDecoder(String token) throws IllegalArgumentException, UnsupportedEncodingException {
		JWTCredentialsVault jwtCV = new JWTCredentialsVault();
		try {
		    Algorithm algorithm = Algorithm.HMAC512(jwtCV.getSecret());
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer("localhost")
		        .build(); //Reusable verifier instance
		    
		    DecodedJWT verifiedJWT = verifier.verify(token);
		            
//		    DecodedJWT decodedJWT = JWT.decode(token);

	        if(verifiedJWT.getIssuer().equals("localhost")) {

	        	JSONObject claims = new JSONObject();
	        	claims.put("email_verified", verifiedJWT.getClaim("email_verified").asBoolean());
	        	claims.put("iss", verifiedJWT.getClaim("iss").asString());
	        	claims.put("exp", verifiedJWT.getClaim("exp").asLong());
	        	claims.put("mfa_verfication", verifiedJWT.getClaim("mfa_verfication").asString());
	        	claims.put("iat", verifiedJWT.getClaim("iat").asLong());
	        	claims.put("email", verifiedJWT.getClaim("email").asString());

	        	return claims;
	        }
	        return new JSONObject().put("error", "Invalid Token");
		}catch(TokenExpiredException exp) {
//			System.out.println("In TokenExpiredException "+exp);
			return new JSONObject().put("error", "Invalid Token, Token already expired");
		}
		catch (JWTVerificationException exception){
		    //Invalid signature/claims
//			System.out.println("In JWTVerificationException "+exception);
			return new JSONObject().put("error", "Invalid Token");
		}
	}
	
}
