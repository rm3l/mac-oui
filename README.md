# mac-oui

[![Build](https://github.com/rm3l/mac-oui/workflows/Build%20and%20Publish%20Docker%20Image/badge.svg)](https://github.com/rm3l/mac-oui/actions?query=workflow%3A%22Build+and+Publish+Docker+Image%22)

<!-- [![Heroku](https://img.shields.io/badge/heroku-deployed%20on%20free%20dyno-blue.svg)](https://mac-oui-api.herokuapp.com/graphiql) -->

[![Docker Stars](https://img.shields.io/docker/stars/rm3l/mac-oui.svg)](https://hub.docker.com/r/rm3l/mac-oui)
[![Docker Pulls](https://img.shields.io/docker/pulls/rm3l/mac-oui.svg)](https://hub.docker.com/r/rm3l/mac-oui)

[![License](https://img.shields.io/badge/license-MIT-green.svg?style=flat)](https://github.com/rm3l/mac-oui/blob/master/LICENSE)


Microservice for looking up manufacturers from MAC addresses, written in Java
and leveraging [Quarkus](https://quarkus.io/), the Supersonic Subatomic Java Framework.

This supports non-interruptable updates and can update the database while the server is running.

An Organizationally Unique Identifier (OUI) is a 24-bit number that uniquely identifies a vendor,
manufacturer, or other organization globally or worldwide.

These are purchased from the Institute of Electrical and Electronics Engineers, Incorporated (IEEE) Registration Authority. 
They are used as the first portion of derivative identifiers to uniquely identify a particular piece of equipment such as Ethernet MAC addresses or other Fibre Channel devices.

In MAC addresses, the OUI is combined with a 24-bit number (assigned by the owner) to form the address.
The first 24 bits of the address are the OUI.

See [this article](http://en.wikipedia.org/wiki/Organizationally_unique_identifier) for further details.


## Using the server

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./gradlew quarkusDev
```

Then visit http://localhost:8080/q/swagger-ui (accessible only in dev mode) for an interactive web UI playground.

### Packaging and running the application

The application can be packaged using `./gradlew quarkusBuild`.
It produces the `mac-oui-1.4.0-SNAPSHOT-runner.jar` file in the `build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

The application is now runnable using `java -jar build/mac-oui-1.4.0-SNAPSHOT-runner.jar`.

If you want to build an _über-jar_, just add the `--uber-jar` option to the command line:
```
./gradlew quarkusBuild --uber-jar
```

### Creating a native executable

You can create a native executable using: `./gradlew build -Dquarkus.package.type=native`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./build/mac-oui-1.4.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling#building-a-native-executable.

### Docker

A Docker repository with the microservice can be found here: https://hub.docker.com/r/rm3l/mac-oui

To fetch the docker image, run:

```bash
docker image pull rm3l/mac-oui
```

To run the server with the default options and expose it on port 8080, run:

```bash
docker container run --rm -p 8080:8080 rm3l/mac-oui
```

### Kubernetes

This microservice is also published to [my Helm Charts repository](https://helm-charts.rm3l.org/), so as to be deployable to a Kubernetes Cluster using [Helm](https://helm.sh/).

It is listed on Artifact Hub : https://artifacthub.io/packages/helm/rm3l/mac-oui

```bash
$ helm repo add rm3l https://helm-charts.rm3l.org
$ helm install my-mac-oui rm3l/mac-oui
```

See https://artifacthub.io/packages/helm/rm3l/mac-oui or https://github.com/rm3l/helm-charts/blob/main/charts/mac-oui/README.md for
all customizable values.


## The API

For simplicity, this microservice exposes the following application endpoints:

### `GET /?mac=<mac-address-or-oui-prefix>`

Example:

```
curl http://localhost:8080/?mac=9C:B6:D0:A0:B0:C0

{
  "data": {
    "comment": null,
    "country": null,
    "manufacturer": "Rivet Networks",
    "prefix": "9C:B6:D0"
  }
}
```

### `GET /{mac-address-or-oui-prefix}`

Example:

```
curl http://localhost:8080/9C-B6-D0

{
  "data": {
    "comment": null,
    "country": null,
    "manufacturer": "Rivet Networks",
    "prefix": "9C:B6:D0"
  }
}
```

## In use in the following apps/services

(If you use this project, please drop me a line at &lt;armel@rm3l.org&gt; 
(or better, fork, modify this file and submit a pull request), so I can list your project(s) here)

* [DD-WRT Companion](https://ddwrt-companion.app), to provide comprehensive and actionable insights about devices connected to a [DD-WRT](https://dd-wrt.com/) router 
* [Androcker](https://play.google.com/store/apps/details?id=org.rm3l.container_companion), a companion app for Docker


## Contribution Guidelines

Contributions and issue reporting are more than welcome. So to help out, do feel free to fork this repo and open up a pull request.
I'll review and merge your changes as quickly as possible.

You can use [GitHub issues](https://github.com/rm3l/mac-oui/issues) to report bugs.
However, please make sure your description is clear enough and has sufficient instructions to be able to reproduce the issue.


## Developed by

* Armel Soro
  * [keybase.io/rm3l](https://keybase.io/rm3l)
  * [rm3l.org](https://rm3l.org) - &lt;armel@rm3l.org&gt; - [@rm3l](https://twitter.com/rm3l)
  * [paypal.me/rm3l](https://paypal.me/rm3l)
  * [coinbase.com/rm3l](https://www.coinbase.com/rm3l)


## Credits / Inspiration

* [oui](https://github.com/klauspost/oui), by @klauspost


## License

    The MIT License (MIT)

    Copyright (c) 2020-2021 Armel Soro

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
