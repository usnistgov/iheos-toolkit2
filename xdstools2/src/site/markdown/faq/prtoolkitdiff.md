# What is the difference between the Public Registry server and XDS Toolkit

Lots.

## Age and maintenance

The Public Registry server is quite old and is missing many modern metadata features. Toolkit is
quite a bit younger. There is no longer any development happening on the Public Registry code base.  Toolkit
development is on going.  All new development will be in toolkit.

## Deployment model

The Public Registry is composed of a collection of services spanning two separete Tomcat installations and
is supported by a PostgreSQL database. The binaries are available and many people have local copies running
in their organizations.

Toolkit is a single WAR file that is intended to be downloaded and run on your machine under Tomcat.

## Usage

The Public Registry is available on the Internet and is used by developers working on client software, mainly
Document Source and Document Consumer implementations.

Toolkit was originally designed to support server developers (Registry and Repository) and has typically
been run on developer's local machine. I evolving the Toolkit code base to make it more relevant as a server.

## What services does each tool offer?

|                         |  Public Registry           | Toolkit               |
| :-----------------:     | :------------------------: | :-------------------: |
| **Document Registry**   |  yes                       |  yes - simulator      |
| Register                |  yes                       |  yes                  |
| Stored Query            |  yes                       |  yes                  |
| Patient Feed            |  Using special tool        | V2                    |
| Metadata Update         |  no                        | yes except Patient ID |
| Async                   | no                         | fall 2016             |
| On-Demand               | no                         | in development        |
| **Document Repository** |  yes                       |  yes - simulator      |


## What is the difference between a simulator and an implementation and the Public Registry?

