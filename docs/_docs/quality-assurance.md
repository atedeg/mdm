---
title: Quality Assurance
layout: static-site-main
---

# Quality Assurance

This section will review the quality assurance practices adopted by the team.

## Conventional Commits

The team agreed on the use of [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) so that versions reflecting the extent of changes
made to the codebase could be generated from the commit messages.

The adoption of this standard forces a particular format for commit messages to be followed, so it is quite easy to get it wrong and produce messages
that do not comply with that format.
Therefore, it would be useful to have a check that rejects all those commits that do not comply with the standard; in this regard, an sbt plugin has
been developed that, taking advantage of git hooks, performs commit checking and in case they do not comply with conventional commits, rejects them.

Although there are other similar tools such as [`husky` + `commitlint`](https://github.com/conventional-changelog/commitlint), these are not native
to sbt and thus require manual intervention to be activated. In fact, if you have these tools available but then they do not "activate" automatically,
then they become useless. What we want to achieve is that as soon as a new user clones the repository and imports the project, then they have already
activated commit check. This is what our `sbt-conventional-commits` plugin does, for more information see the
[plugin documentation](https://github.com/nicolasfara/sbt-conventional-commits).

> Initially, a gradle plugin was developed to check commit messages.
> Then having the need to work with sbt it was decided to rewrite it reflecting all the functionality.

## Semantic release

semantic-release.
