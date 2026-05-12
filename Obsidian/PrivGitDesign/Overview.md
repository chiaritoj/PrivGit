## Purpose

This application is a self-hosted Git hosting platform inspired by systems such as GitHub, GitLab, and Gitea.

The goal of the project is to provide:

- Remote Git repository hosting
    
- SSH-based Git operations
    
- Repository management APIs
    
- Metadata storage and permission handling
    
- Web-based repository browsing
    
- A foundation for future pull requests, issues, and CI features
    

Unlike traditional setups that rely heavily on system-level SSH configuration and shell scripting, this platform embeds much of the control logic directly into the application.

The application itself acts as:

- A REST API server
    
- An SSH Git server
    
- A repository orchestration layer
    
- A metadata service
    

---

# High-Level Architecture

```text
                     ┌─────────────────────┐
                     │ Frontend / Web UI   │
                     │ React / Next.js     │
                     └─────────┬───────────┘
                               │ HTTP
                     ┌─────────▼───────────┐
                     │   Spring Boot App   │
                     │─────────────────────│
                     │ REST API            │
                     │ Authentication      │
                     │ Repository Service  │
                     │ Permission Service  │
                     │ Git Metadata APIs   │
                     │ SSH Git Server      │
                     └───────┬───────┬─────┘
                             │       │
                       JGit API   Apache MINA SSHD
                             │       │
                             └──┬────┘
                                │
                     ┌──────────▼──────────┐
                     │ Bare Git Repos      │
                     │ /srv/git/...        │
                     └──────────┬──────────┘
                                │
                     ┌──────────▼──────────┐
                     │ PostgreSQL          │
                     │ users/repos/keys    │
                     └─────────────────────┘
```

---

# Core Technologies

|Component|Purpose|
|---|---|
|Spring Boot|Main backend application framework|
|Apache MINA SSHD|Embedded SSH server for Git operations|
|PostgreSQL|Metadata and permission storage|
|Native Git binaries|Initial Git transport implementation|
|JGit|Git metadata parsing and eventual transport replacement|
|Docker Compose|Deployment and local development|

---

# Core System Responsibilities

The backend application is responsible for:

## User Management

- User registration
    
- Login/authentication
    
- Session or token management
    
- SSH key management
    

---

## Repository Management

- Create repositories
    
- Delete repositories
    
- Repository visibility settings
    
- Branch protection metadata
    
- Permission assignment
    

---

## SSH Git Operations

The system accepts Git SSH operations directly.

Examples:

```bash
git clone ssh://git@server:2222/alice/project.git
```

```bash
git push origin main
```

The backend:

1. Accepts the SSH connection
    
2. Authenticates the user's SSH key
    
3. Validates repository permissions
    
4. Executes the Git operation
    
5. Streams Git protocol data back to the client
    

---

## Git Metadata APIs

The backend exposes repository metadata for the frontend.

Examples:

- Commit history
    
- Branch lists
    
- File browsing
    
- Diffs
    
- README rendering
    
- Pull request metadata
    

---

# Repository Storage Model

Repositories are stored as bare Git repositories.

Example structure:

```text
/srv/git/
    alice/
        project.git
    bob/
        notes.git
```

Bare repositories contain:

- Git objects
    
- Refs
    
- Commit history
    
- Packfiles
    

They do not contain checked-out working directories.

---

# Spring Boot Application Structure

A possible package layout:

```text
com.example.githost
├── auth
├── config
├── git
├── permissions
├── pullrequests
├── repositories
├── ssh
├── users
└── web
```

---

# Major Backend Modules

## auth

Responsible for:

- Login
    
- Password hashing
    
- JWT/session generation
    
- Authentication middleware
    

---

## ssh

Responsible for:

- Apache MINA SSHD integration
    
- SSH key authentication
    
- Git command parsing
    
- Git command execution
    

---

## git

Responsible for:

- JGit integration
    
- Commit parsing
    
- Diff generation
    
- Branch listing
    
- Tree traversal
    
- Packfile operations later
    

---

## repositories

Responsible for:

- Repository lifecycle management
    
- Repository creation/deletion
    
- Filesystem path management
    
- Visibility configuration
    

---

## permissions

Responsible for:

- Access control
    
- User roles
    
- Push/pull permissions
    
- Branch protections later
    

---

# PostgreSQL Data Model

## users

Stores application users.

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

---

## ssh_keys

Stores user SSH public keys.

```sql
CREATE TABLE ssh_keys (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    public_key TEXT NOT NULL
);
```

---

## repositories

Stores repository metadata.

```sql
CREATE TABLE repositories (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT REFERENCES users(id),
    name TEXT NOT NULL,
    visibility TEXT NOT NULL,
    repo_path TEXT NOT NULL
);
```

---

## repo_permissions

Stores access permissions.

```sql
CREATE TABLE repo_permissions (
    user_id BIGINT REFERENCES users(id),
    repo_id BIGINT REFERENCES repositories(id),
    role TEXT NOT NULL
);
```

---

# Embedded SSH Server Design

The application embeds an SSH server using Apache MINA SSHD.

This avoids:

- Manual OpenSSH key management
    
- authorized_keys scripting
    
- Shell wrapper complexity
    

The application itself becomes the SSH endpoint.

---

# SSH Flow

## Step 1 — User connects

```bash
ssh git@server -p 2222
```

---

## Step 2 — SSH key authentication

Apache MINA SSHD invokes:

```java
PublickeyAuthenticator
```

The backend:

1. Reads the public key
    
2. Queries PostgreSQL
    
3. Identifies the user
    
4. Accepts or rejects authentication
    

---

## Step 3 — Git command received

Git sends commands such as:

```text
git-upload-pack '/alice/project.git'
```

or:

```text
git-receive-pack '/alice/project.git'
```

---

## Step 4 — Permission validation

The backend validates:

- Repository existence
    
- User access rights
    
- Push/pull permissions
    

---

## Step 5 — Git execution

The backend executes Git transport operations.

Initially, this uses native Git binaries.

Example:

```bash
git-receive-pack
```

or:

```bash
git-upload-pack
```

---

# Initial Git Strategy

The initial implementation should rely on native Git binaries.

Reasons:

- Battle-tested protocol implementation
    
- Full compatibility with Git clients
    
- Efficient packfile handling
    
- Lower implementation complexity
    

The backend orchestrates Git rather than reimplementing it.

---

# JGit Integration Strategy

JGit is initially used for metadata operations.

Examples:

- Reading commits
    
- Listing branches
    
- Generating diffs
    
- Traversing trees
    
- Parsing refs
    

Example usage:

```java
Iterable<RevCommit> commits = git.log().call();
```

---

# Long-Term JGit Goals

Eventually, the system may replace native Git transport components with JGit-based implementations.

Potential future features:

- Custom packfile optimization
    
- In-process Git transport
    
- Custom object database
    
- Storage abstraction
    
- Advanced repository indexing
    

---

# REST API Design

Example API endpoints:

```text
POST /api/auth/register
POST /api/auth/login

POST /api/repos
GET  /api/repos/{id}
GET  /api/repos/{id}/commits
GET  /api/repos/{id}/branches
GET  /api/repos/{id}/tree

POST /api/ssh-keys
DELETE /api/ssh-keys/{id}
```

---

# Security Model

## SSH Authentication

Users authenticate with SSH public keys.

---

## Authorization

The backend validates:

- Repository access
    
- Push permissions
    
- Visibility restrictions
    

---

## Command Restrictions

Only Git-related commands are allowed.

Examples:

Allowed:

- git-upload-pack
    
- git-receive-pack
    

Rejected:

- Arbitrary shell commands
    
- Interactive shells
    

---

# Docker Deployment

The entire platform should be containerized.

Example architecture:

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
      - "2222:2222"

  postgres:
    image: postgres
```

---

# Recommended Development Phases

# Phase 1 — Core Repository Hosting

Goals:

- Repository creation
    
- Bare repository storage
    
- Local Git operations
    
- PostgreSQL metadata
    

---

# Phase 2 — Embedded SSH Git Server

Goals:

- Apache MINA SSHD integration
    
- SSH key authentication
    
- Push/pull support
    
- Repository authorization
    

At this point, real Git clients can clone and push.

---

# Phase 3 — Metadata APIs + Web UI

Goals:

- Commit browsing
    
- Branch listing
    
- File tree rendering
    
- Diff rendering
    
- Frontend integration
    

---

# Phase 4 — Advanced Git Internals

Goals:

- JGit transport exploration
    
- Packfile optimization
    
- Background maintenance
    
- Repository indexing
    
- Performance improvements
    

---

# Future Expansion Ideas

Potential long-term features:

- Pull requests
    
- Issues
    
- CI/CD pipelines
    
- Webhooks
    
- Actions/runners
    
- Repository search
    
- Syntax highlighting
    
- Distributed storage
    
- Object deduplication
    
- Custom Git protocol optimizations
    

---

# Educational Value

This project combines:

## Backend Engineering

- REST APIs
    
- Authentication
    
- Database modeling
    
- Service architecture
    

---

## Systems Engineering

- SSH protocol handling
    
- Process orchestration
    
- Filesystem design
    
- Concurrent operations
    

---

## Git Internals

- Packfiles
    
- Object databases
    
- Refs
    
- Commit DAGs
    
- Git transport operations
    

---

## Infrastructure Engineering

- Docker
    
- Deployment
    
- Linux environments
    
- Persistent storage
    

---

# Summary

This architecture creates a self-contained Git hosting platform where the backend application directly controls:

- Authentication
    
- SSH connections
    
- Repository management
    
- Metadata APIs
    
- Git orchestration
    

The design intentionally balances:

- Realism
    
- Educational depth
    
- Feasibility
    
- Extensibility
    

The system begins by orchestrating existing Git tooling and can progressively evolve toward deeper custom Git internals over time.