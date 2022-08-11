---
title: Deployment Strategy
layout: static-site-main
---

# Deployment Strategy

A good deployment strategy is crucial for the success of the project.
One of the main goals we want to achieve is the adoption of the continuous release strategy.
This implies, from our point of view, having a robust test suite, an adequate DVCS workflow and tools to automate the releases.
The DVCS workflow adopted is quite similar to `git-flow` but with substantial changes: we use the "feature branch" as intended,
where each feature is well-isolated ad defined.

A beta branch is used to publish a non-stable version of the software despite a beta-release will be triggered.
The main branch is intended to be the only stable branch, where a push on this branch triggers a new stable release.
The beta branch has been useful especially in the early stages of development as it generates pre-releases therefore
to be considered unstable and subject to changes.
To prevent mistakes like pushing on the wrong branch, some branch protection rules were defined.
The branches covered by branch protection are main and beta, namely that triggers a release.
Branch protection caused a problem that has been fixed: since no user can push directly on those branches,
tools like `semantic-release` couldn't push stuff on those branches.
To address this problem we created a team application `atedeg-bot` that has the push permission on those branches so that the
release process succeeded without fails.

This workflow leads us to add features and fixes by PRs (Pull Requests) allowing code review and comments before the merge.
In this way, each team member will have awareness of the others' code ensuring an overview of the project.

In addition, this type of workflow makes classic branch develop totally unnecessary:
having a robust test suite joint with the use of semantic-release, allows you to merge directly to main (or beta)
and only relevant changes trigger a new release without having to worry about when to make a new one
(as opposed to what happens when you have to deal with develop).

An example of the workflow adopted by the team is shown in the image below.

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
