| ID | INFRA-1 |
| --- | --- |
| Function | Ubuntu 20.04 or Higher |
| Description | All servers must run a secure version of Ubuntu. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | Only servers running Ubuntu 20.04 or higher are allowed. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | INFRA-2 |
| --- | --- |
| Function | Port Control |
| Description | All servers must run in a sealed VPC. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | All servers must exist in a VPC protected by a firewall that has only port 80 open. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | INFRA-3 |
| --- | --- |
| Function | DB Control |
| Description | All databases must run in a sealed VPC. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | All databases must run in a VPN protected by a firewall.  They may not be publically accessible. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | INFRA-4 |
| --- | --- |
| Function | App Control |
| Description | All applications must run in a container. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | All applications must run in a container providing sandboxing capability without access to the file system. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | INFRA-5 |
| --- | --- |
| Function | App Deployment |
| Description | All applications must run in a container. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | All applications will be deployed using a managed instance of Kubernetes. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | INFRA-6 |
| --- | --- |
| Function | Monitoring |
| Description | All servers must be monitored. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | Servers must have their disk space, cpu, and memory monitored to detect health problems. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | INFRA-7 |
| --- | --- |
| Function | Load Balancers |
| Description | Load balancers may be the only gateway into the firewall. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | Publically accessible load balancers must sit in front of the firewall redirecting traffic to HTTPS and providing SSL. The load balancer will be responsible for forwarding traffic over HTTP into the VPC.. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |