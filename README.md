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
