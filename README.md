[![Build Status](https://travis-ci.org/loonwerks/BriefCASE.svg?branch=master)](https://travis-ci.org/loonwerks/BriefCASE)

# BriefCASE

TBD

## User Guide

TBD

## Development Guide

TBD

### Branches and Tags

The BriefCASE tool development occurs on mainly on the **master**
branch of this repository.  The 'master' branch contains the releases
and the HEAD of this branch should contain the latest release.
Individual development efforts should be completed on branches from
the master branch and then merged back into the master via a **pull
request** including informal peer review.

It is the general policy to consider releases as final; no updates to
releases are made.  Instead all updates are made to the master branch
and incorporated into the next release.

Occasionally it is desirable to create additional special-purpose
branches for responding to issue/bug reports.  These should be made
from a branch from the release point and, if possible, merged onto the
master branch prior to completion of the next release cycle.  If such
a branch spans a release, the developer should be merge the updated
master branch onto the special-purpose branch to capture the updated
versioning information and to avoid conflicts merging back into the
develop branch.

Branch names should be chosen to describe the activity to be taken on
the branch.  For example, to develop a new feature 'A', the branch
might be named 'develop-feature-A.'  Or to fix an issue recorded in
the issue base the branch might be named 'fix-issue-x' where x is the
sequence number assigned to the issue.

Tags are typically reserved for releases, but may be used to mark
special points in the development process.

### Continuous Integration / Continuous Deployment

BriefCASE continuous integration is carried out at
[Amazon CodeBuild](https://aws.amazon.com).  Builds of developmental
snapshots are uploaded to the AWS bucket
[BriefCASE snapshots](http://ca-trustedsystems-dev-us-east-1.s3-website-us-east-1.amazonaws.com/p2/snapshots/briefcase/) page.
Additionally, CodeBuild conducts builds and tests in response to pull
requests against the BriefCASE repository.

### Releases

Since BriefCASE has converted to the [Semantic
Versioning](https://semver.org/) model, each of the plugins that
compose the BriefCASE feature now have
individual version numbers that reflect the updates to that plugin.
Thus, they are no longer synchronized to the feature versions.

The BriefCASE code base has been modified to enable Eclipse plugin API
analysis which will result in error messages and warnings (in the
"Problems" tab of the IDE) to guide developers as to how and when to
update version numbers and apply API tags such as @since to added or
modified elements.  The Oomph version management analysis has also
been enabled to assist with version number markings.

The process for a release updates master branch with the release
version number, commits the master branch version number updates,
builds the release version, updates the version numbering to the new
development snapshot numbers, and finally commits the master branch
with the new development version numbering.  To accomplish the release
preparation, the BriefCASE release process applies the [Tycho Release
Workflow](https://wiki.eclipse.org/Tycho/Release_Workflow) to help
automate the process.  The steps in the release workflow are as
follows:

1. Prepare for the release by running the release workflow plugin:

   `mvn release:prepare`

   During this process maven will ask to confirm all of the plugin and
   feature versions and for the tag to applied to the
   release. Release tags shall be of the form 'x.y.z-RELEASE' where
   `x` is the major version number, `y` is the minor version number,
   and `z` is the patch version number.

1. Ordinarily one would expect to actually perform the release using
   the customary `mvn release:perform -Dgoals="clean verify"` command.
   However, this appears to run into difficulty fetching the appropriate
   tag to build.  Instead do the following:

   `git checkout x.x.x-RELEASE`
   `mvn clean verify`

   where `x.x.x` is the version number to be released.

1. Commit the release binaries into the companion releases repository at
   git@github.com:loonwerks/BriefCASE-Updates.git:

   `cp -r <path to built BriefCASE release>/repository/target/repository briefcase_<release-version-number>`

   Update the contents of the compositeArtifacts.xml and compositeContent.xml files.

   `git add ...`

   `git commit -m "Add BriefCASE <release-version-number>"`

1. Finally, push the update stable branch to the origin repository:

   `git push`

Note that the previous instructions assume that the developer has set
up appropriate ssh keys such that interactive query for authentication
is necessary.  If this is not done, the `release:prepare` and
`release:perform` steps will apparently just hang when such
interaction is required as they are in fact headless operations.
Alternatively to setting up ssh keys, the developer may add necessary
authenication information to the maven settings as described in [Tycho
Release Workflow -- Configure the
SCM](https://wiki.eclipse.org/Tycho/Release_Workflow#Configure_the_SCM).
