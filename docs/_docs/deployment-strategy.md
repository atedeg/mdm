---
title: Deployment Strategy
layout: static-site-main
---

# Deployment Strategy

A good deployment strategy is crucial for the success of a project.
One of the main goals we want to achieve is the adoption of the continuous release strategy.
We decided to adopt a continuous release strategy: this implied having a robust test suite,
an adequate DVCS workflow and tools to automate the releases.

## Adopted DVCS Workflow

The DVCS workflow we adopted is quite similar to `git-flow` but with substantial changes:
we use the `feature` branch as intended, with each feature well-isolated and defined.
Moreover, we used a `beta` branch to publish a non-stable version of the software; this can be
quite handy as it generates pre-releases that can be beta-tested before releasing a stable version.
Lastly, the `main` branch is intended to be the only stable branch: a push on this branch triggers
a new stable release.

We also added branch protection to the `main` and `beta` branches to prevent mistakes like pushing
on the wrong branch and inadvertently triggering a release.
These rules, however, also prevented tools like `semantic-release` from pushing on those branches.
To overcome this issue we created a team application `atedeg-bot` that has the push permission on
those branches so that the release process could succeed without failures.

We also added features and fixes by using GitHub's pull requests, allowing for code review and
comments from other team members before the code could be merged.
This way each team member can still have an overview of the other's work and point out possible
problems or ideas to improve it.

In addition, this type of workflow makes the classic `develop` branch unnecessary:
having a robust test suite joint with the use of semantic-release, allows one to merge directly to
`main` (or `beta`); only relevant changes trigger a new release without having to worry about when
to make a new one (as opposed to what happens when one has to deal with the `develop` branch).

An example of the workflow adopted by the team is shown in the image below:

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
