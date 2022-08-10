---
title: Deployment Strategy
layout: static-site-main
---

# Deployment Strategy

```mermaid
gitGraph
  commit id: "chore: ..."
  commit id: "build: ..."
  commit id: "chore: ..."
  commit id: "ci: ..."
  branch beta
  commit tag: "1.0.0-beta.1"
  branch feat/feature-1
  checkout feat/feature-1
  commit id: "feat: feature 1"
  commit id: "feat: feature 2"
  checkout beta
  merge feat/feature-1 tag: "1.0.0-beta.2"
  branch feat/feature-2
  commit id: "feat: feature 3"
  checkout beta
  merge feat/feature-2 tag: "1.0.0-beta.3"
  checkout main
  merge beta tag: "1.0.0"
  branch fix/fix-2
  commit id: "fix: fix1"
  commit id: "fix: fix2"
  checkout main
  merge fix/fix-2 tag: "1.0.1"
```