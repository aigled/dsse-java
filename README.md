# DSSE Java

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build](https://github.com/aigled/dsse-java/actions/workflows/build.yml/badge.svg)](https://github.com/aigled/dsse-java/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=aigled_dsse-java&metric=alert_status&token=23801b6b2ef2154e031ac6e80e544ff5a7d398ed)](https://sonarcloud.io/summary/new_code?id=aigled_dsse-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=aigled_dsse-java&metric=coverage&token=23801b6b2ef2154e031ac6e80e544ff5a7d398ed)](https://sonarcloud.io/summary/new_code?id=aigled_dsse-java)

A Java implementation of the [Dead Simple Signing Envelope (DSSE)](https://github.com/secure-systems-lab/dsse)
specification. This library allows you to sign and verify arbitrary data in a consistent, interoperable format.

## Overview

DSSE is designed to be a simple, secure envelope for metadata. It avoids the complexity of other signing formats by
providing a predictable way to wrap a payload and its signatures. This library provides the tools to create, sign, and
verify these envelopes within the Java ecosystem.

## Requirements

- Compiled with JDK 25
- Requires Java 17 or higher for runtime

## Features

- Fully compliant with the DSSE specification.
- Support for multiple signature types.

## Installation

### Gradle (Kotlin DSL)

Add the following to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.aigled:dsse-java:1.0.1")
}
```

### Maven

Add the following to your `pom.xml`:

```xml

<dependency>
    <groupId>io.github.aigled</groupId>
    <artifactId>dsse-java</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Usage

### Prerequisites

Having a private and public key pair is required for signing and verifying. You can generate one using OpenSSL, for
example:

```bash
openssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-256 -pkeyopt ec_param_enc:named_curve -out private_key.pem
openssl pkey -in private_key.pem -pubout -out public_key.pem
```

### Signing

Create a DSSESigner with the private key you want to use for signing.
Bellow is an example of using the [Bouncy Castle API](https://www.bouncycastle.org/documentation/documentation-java/) to load the private key.

```java
try (Reader reader = Files.newBufferedReader(Path.of("path/to/private.pem"));
     PEMParser pemParser = new PEMParser(reader)) {

    PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
    PrivateKey privateKey = converter.getPrivateKey(privateKeyInfo);
    
    DSSESigner signer = new ECDSASigner("SHA256withECDSA", privateKey);
}
```

Create a DSSEEnvelope with the payload you want to sign and sign with the signer you created above.

```java
DSSEEnvelope envelope = new DSSEEnvelope(json.getBytes(), "application/json");
envelope.sign("myKeyId", signer);
```

Create a DSSESerializer and serialize the envelope to JSON.

```java
DSSESerializer serializer = new Jackson2JsonDSSESerializer();
String jsonEnvelope = serializer.serialize(envelope);
```

The resulting JSON after formatting will look like this :

```json
{
  "payload": "ewogICJkYXRhIjogInRlc3QiLAp9Cg==",
  "payloadType": "application/json",
  "signatures": [
    {
      "keyid": "myKeyId",
      "sig": "MEUCIAPVIklhJHmODjwSbuYk8966XQU0aG4k2gAfg0OYOPM2AiEAu9suuKoBCUcNFqJO83HwobjPCmGWdu5YWebdNxl/xWQ="
    }
  ]
}
```

### Verifying

Create a DSSEDeserializer and deserialize the JSON envelope you want to verify.

```java
String jsonEnvelope = "{...}";
DSSEDeserializer deserializer = new Jackson2JsonDSSEDeserializer();
DSSEEnvelope envelope = deserializer.deserialize(jsonEnvelope);
```

Create a DSSEVerifier with the public key you want to use for verification.
Bellow is an example of using the [Bouncy Castle API](https://www.bouncycastle.org/documentation/documentation-java/) to load the public key.

```java
try (Reader reader = Files.newBufferedReader(Path.of("path/to/public.pem"));
     PEMParser pemParser = new PEMParser(reader)) {

    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
    SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
    PublicKey publicKey = converter.getPublicKey(publicKeyInfo);
    
    DSSEVerifier verifier = new ECDSAVerifier("SHA256withECDSA", publicKey);
}
```

Create a DSSEVerificationPolicy and configure it according to your needs.

```java
var trustedVerifiers = Map.of("myKeyId", verifier);
DSSEVerificationPolicy policy = new ThresholdVerificationPolicy(1, false, trustedVerifiers);
```

Verify the envelope with the policy you created above and check the result.

```java
boolean isVerified = envelope.verify(policy);
```

TIP: You can use the website  https://dsse.io to verify your JSON envelope against the public key.
