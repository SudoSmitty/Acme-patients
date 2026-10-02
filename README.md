# Acme Patient Records API

Owned by the **Clinical Systems** team. A small Spring Boot 3.5 / Java 21 service that serves
de-identified patient records.

| Endpoint | Description |
|---|---|
| `GET /api/patients` | List patients |
| `GET /api/patients/{id}` | One patient |
| `GET /api/info` | Version, environment, rollout track, pod |
| `GET /actuator/prometheus` | Metrics scraped for canary verification |

## Delivery

This repo has no pipeline of its own. Every push to `main` runs the shared **Acme Golden Path**
pipeline in Harness (the same one the GitLab-hosted Device Telemetry API uses):

1. Security Scans (platform-owned, locked): secrets, SAST, SCA
2. App Team Extensions (owned by this team): `API contract lint` on `api/openapi.yaml`
3. Build & Publish: tests, image to Harness Artifact Registry, container scan, SBOM, SLSA provenance
4. Canary to non-prod with AI verification, approval, canary to prod

Kubernetes manifests live in `deploy/k8s` and are rendered by Harness at deploy time.

## Run locally

```bash
mvn package && java -jar target/app.jar
curl localhost:8080/api/patients
```

## Delivery

Every push to `main` runs the **Acme Golden Path** pipeline in Harness: platform security scans,
the team's own checks (API contract lint), build + SBOM + SLSA provenance into Harness Artifact
Registry, canary to `acme-nonprod` with automated verification, approval, then canary to `acme-prod`.
