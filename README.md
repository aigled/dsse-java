# DSSE Java

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Java Version](https://img.shields.io/badge/Java-25-orange)
[![Build](https://github.com/aigled/dsse-java/actions/workflows/build.yml/badge.svg)](https://github.com/aigled/dsse-java/actions/workflows/build.yml)

A Java implementation of the [Dead Simple Signing Envelope (DSSE)](https://github.com/secure-systems-lab/dsse)
specification. This library allows you to sign and verify arbitrary data in a consistent, interoperable format.

## Overview

DSSE is designed to be a simple, secure envelope for metadata. It avoids the complexity of other signing formats by
providing a predictable way to wrap a payload and its signatures. This library provides the tools to create, sign, and
verify these envelopes within the Java ecosystem.

## Features

- Fully compliant with the DSSE specification.
- Support for multiple signature types.

## Installation

### Gradle (Kotlin DSL)

Add the following to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.aigled:dsse-java:1.0.0")
}
```

### Maven

Add the following to your `pom.xml`:

```xml

<dependency>
    <groupId>io.github.aigled</groupId>
    <artifactId>dsse-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Signing

Create a DSSESigner with the private key you want to use for signing.

```java
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
keyPairGenerator.initialize(256);
KeyPair keyPair = keyPairGenerator.generateKeyPair();

DSSESigner signer = new ECDSASigner("SHA256withECDSA", keyPair.getPrivate());
```

Create a DSSEEnvelope with the payload you want to sign and sign with the signer you created above.

```java
DSSEEnvelope envelope = new DSSEEnvelope(json.getBytes(), "application/json");
envelope.sign("myKeyId",signer);
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