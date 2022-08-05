---
title: Quality Assurance
layout: static-site-main
---

# Quality Assurance

This section will review the quality assurance practices adopted by the team.

## Conventional Commits

We adopts [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) as the convention for commit messages.
It's a lightweight convention on top of commit messages which provides an easy set of rules for creating an explicit commit history, which makes it
easier to write automated tools on top.

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

Assigning versions to code is a controversial activity. People often assign versions based on intuition or even worse by going randomly.
This is precisely why the [SemVer](https://semver.org/) standard was made.
SemVer provides guidelines on how to properly assign a version based on some rules.
Even following the SemVer guidelines there remains the problem of figuring out which version to assign: identifying the extent of the changes made
between versions to determine the new version is not an easy task. Doing so requires re-reading the commit history and getting an idea of what was
changed and no one typically wants to do that.
In addition, this kind of task is complicated by the fact that many commit messages may be ambiguous or may not reflect the actual changes.

If a standard was adopted for commit messages, such as `conventional-commit`, then it would be much easier to determine the extent of changes by
analyzing only commit messages.
Also, by following a standard for messages, automated tools would be able to automatically analyze and determine the version to be assigned.

Tools like [`semantic-release`](https://semantic-release.gitbook.io/semantic-release/) automate version assignment as well as manage the release
of artifacts. This removes the immediate connection between human emotions and version numbers, strictly following the SemVer specification and 
communicating the impact of changes to consumers.
This tool, in addition to automatically determining the version to be assigned, can generate (and update) the CHANGELOG file reporting in an organized
manner all the changes made in the various versions; it interacts with GitHub issues and PRs by creating comments concerning the releases made.

Automating the entire process of versioning and releasing artifacts gives the ability to focus solely on project development without worrying about
project management tasks, minimizing the possibility of errors.

Our workflow concerning semantic-release is organized in the following steps:
1. The `commit-analyzer` plugin analyze the commits messages from the last tag to the latest commit in oder to determinate the version bump to apply
2. The `release-notes-generator` plugin generate the changelog with the modifications since the last tag
3. The `changelog` plugin update the `CHANGELOG.md` file in the repo with the changelog generate at the step above
4. With the plugin `exec` we specify the commands needed in order to publish all the artefacts and the documentation site
   - The first command release all the `jar`s to the _Maven Central_
   - The second command generate the documentation site
   - The third command publish the `Dockerfile` of each sub project to the [docker hub](https://hub.docker.com/)
5. The `git` plugin push the updated `CHANGELOG.md` into the repo
6. The plugin `github` generate the release and comments all the issue closed since the last tag

For a more in-depth description take a look at the file [`.releaserc.yml`](https://github.com/atedeg/mdm/blob/main/.releaserc.yml).
