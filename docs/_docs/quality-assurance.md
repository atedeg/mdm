---
title: Quality Assurance
layout: static-site-main
---

# Quality Assurance

This section will review the quality assurance practices adopted by the team.

## Conventional Commits

We decide to adopts [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) as the convention for commit messages.
It's a lightweight convention on top of commit messages which provides an easy set of rules for creating an explicit commit history, which makes it
easier to write automated tools on top.

The adoption of this standard forces a particular format for commit messages to be followed, so it is quite easy to get it wrong and produce messages
that do not comply with that format.
Therefore, it would be useful to have a check that rejects all those commits that do not comply with the standard;
in this regard, we developed an sbt plugin that, by automatically creating a git hook,
allows to check all commits and reject them in case they do not comply with the conventional commits standard.

Although there are other similar tools such as [`husky` + `commitlint`](https://github.com/conventional-changelog/commitlint), these are not native
to sbt and thus require manual intervention to be activated. However, one could forget to activate these tools rendering them useless.
What we wanted to achieve is that as soon as a new user clones the repository and imports the project, they already have the commit check enabled.
This is what our `sbt-conventional-commits` plugin does, for more information see the
[plugin documentation](https://github.com/nicolasfara/sbt-conventional-commits).

> Initially, a gradle plugin was developed to check commit messages.
> Then having the need to work with sbt it was decided to rewrite it reflecting all the functionality.

## Semantic release

Assigning versions to code is a controversial activity. People often assign versions based on intuition or, even worse, randomly.
This is precisely why the [SemVer](https://semver.org/) standard was made:
SemVer provides guidelines on how to properly assign a version based on some rules.
Even following the SemVer guidelines there is still the problem of figuring out which version to assign: identifying the extent of the changes made
between versions to determine the new version is not an easy task. Doing so requires re-reading the commit history and getting an idea of what was
changed and no one typically wants to do that.
In addition, this kind of task is complicated by the fact that many commit messages may be ambiguous or may not precisely reflect the actual changes.

By adopting a convention for commit messages, such as `conventional-commit`, then it is much easier to determine the extent of changes by simply
analyzing commit messages. Moreover, automated tools can automatically perform this task and determine the version to be assigned.

Tools like [`semantic-release`](https://semantic-release.gitbook.io/semantic-release/) automate version assignment and automatically manage the release
of artifacts. Strictly following the SemVer specification, there is no longer room for human intervention in assigning version numbers that are
determined automatically and can reliably and consistently communicate the impact of changes to consumers.
This tool, in addition to automatically determining the version to be assigned, can generate (and update) the CHANGELOG file reporting in an organized
manner all the changes made in the various versions; it interacts with GitHub issues and PRs by creating comments concerning the releases made.

Automating the entire process of versioning and releasing artifacts gives the ability to focus solely on project development without worrying about
project management tasks, minimizing the possibility of errors.

Our workflow concerning semantic-release is organized in the following steps:

1. The `commit-analyzer` plugin analyzes the commit messages from the last tag to the latest commit in oder to determinate the version bump to apply
2. The `release-notes-generator` plugin generates the changelog with the changes since the last tag
3. The `changelog` plugin updates the `CHANGELOG.md` file in the repo with the changelog generated at the step above
4. With the plugin `exec` we specify the commands needed in order to publish all the artefacts and the documentation site
   - The first command releases all the `jar`s to [_Maven Central_](https://search.maven.org/search?q=dev.atedeg.mdm)
   - The second command generates the documentation site
   - The third command publishes the `Dockerfile` of each sub project to the [Docker Hub](https://hub.docker.com/)
5. The `git` plugin pushes the updated `CHANGELOG.md` into the repo
6. The `github` plugin generates the release and comments all the issues closed since the last tag

For a more in-depth description take a look at the file [`.releaserc.yml`](https://github.com/atedeg/mdm/blob/main/.releaserc.yml).

## Scalafmt

The tool performs code formatting over the entire code base, helping the team to have a uniform code style.
[Scalafmt](https://scalameta.org/scalafmt/) is available via an [sbt plugin](https://github.com/scalameta/sbt-scalafmt)
that provides several tasks to format the code or check if the code is formatted properly.
Specifically, the `scalaftmCheckAll` task is used in CI/CD to check if the pushed code is formatted properly, in the case of unformatted code,
the workflow fails to prevent the merge of unformatted code.

For more details about the rules used by the team, please take a look at the
[`.scalafmt.conf`](https://github.com/atedeg/mdm/blob/main/.scalafmt.conf) configuration file.

## Scalafix

Scalafix helps the developers refactoring the code, spot bad programming practices, and linting the code.

The tool is available via an [sbt plugin](https://github.com/scalacenter/sbt-scalafix) which provides a task to check the project.
The team has agreed on using all the rules except for `UniversalEquality` because of a problem with scala 3.

The task mentioned above is used in CI/CD to enforce the rules and prevent the merge of code with any kind o problems.

For more details about the rules used by the team, please take a look at the
[`.scalafix.conf`](https://github.com/atedeg/mdm/blob/main/.scalafix.conf) configuration file.

## Wartremover

[Wartremover](https://www.wartremover.org/) helps scala developers by removing some of the language’s nastier features.
Its main goal is to help you write safe and correct software without having to constantly double-check yourself.

Wartremover is available via an [sbt plugin](https://github.com/wartremover/wartremover)
which provides some tasks to check the correctness of the project.

The team has agreed to use all warts except for `Overloading` and `Equals` because of scala 3.
The former wart was disabled because generates false positives in some extension methods on different types but with the same method name.
The latter was disabled because of some limitations with the new ADT syntax in scala 3.

Again, in CI/CD the task is executed to enforce the code quality and prevent the merge of problematic code.

## ~~Scoverage~~ JaCoCo & Codecov

Regarding code coverage, it was initially decided to use Scoverage.
However, for Scala 3 it is still not fully compatible: the use of Scala 3.2.0-RC1 or higher is mandatory.
Although a test was done with Scala 3.2.0-RC2, due to macros used in the project, Scoverage crashes badly.
For convenience, we fell back to JaCoCo, which while not designed to operate directly with scala, produces an acceptable coverage report.
Nevertheless, JaCoCo has several limitations in using it with Scala, so the reports it generates are inaccurate and unreliable.
Unfortunately, at the time of writing, there are no alternatives to Scoverage and JaCoCo.

The reports generated by JaCoCo are taken over by [Codecov](https://about.codecov.io/), which is responsible for verifying that for each PRs
the coverage does not fall more than 5% and for generating graphical reports that are accessible [here](https://app.codecov.io/gh/atedeg/mdm).
