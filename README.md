# Introduction

The purpose of this project is to serve as an example for CPS 420: Software Engineering.

## Idea

The idea is to create a cloud-hosted todo list for the president that has multiple roles and levels.

## Roles

- None: Users without a role, or the general public, can view / submit unclassified todos for the president, but they cannot submit classified, secret or top secret todos.
- Classified: Classified Users can view / submit classified or unclassified todos, but not secret or top secret.
- Secret: Secret Users can view / submit secret, classified or unclassified todos, but not top secret.
- Top Secret: Top Secret Users can view / submit todos of any level.
- Aids: Aids can change the priority or todos as well as mark them done, but they cannot submit new todos.

## Structure

Formal requirements for the project are in the `requirements/` folder written in Markdown using a modified form of the IEEE requirements format.  Use case diagrams are also contained in that folder.

Database diagrams are placed in the `architecture/` folder.  The project uses a PostgreSQL backend.

The backend for the project is placed in the `backend/` folder.  The backend is written in Spring Boot / Java 17.

The frontend for the project is placed in the `frontend/` folder.  The frontend is written in Vue / Nuxt.

The Digital Ocean infrastructure setup is contained in the `terraform/` folder.  The templates here create a VPC, firewall, load balancer, kubernetes cluster and database on Digital Ocean for hosting the project. Note that a domain is required to run the template.

The ansible scripts for populating the Kubernetes cluster with information are contained in the `ansible/` folder. The kubernetes cluster contains an instance of prometheus, the prometheus alerter, the frontend, the backend and Fusion Auth.