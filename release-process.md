# Release Process
NOTE: GitFlow is the expected development model, where feature development is performed in a branch off development and merged to development via Pull Request.

NOTE: The POM structure uses a property to define the version and this does not work correctly with the `mvn release` process. DO NOT USE IT.

NOTE: we are not using the typical SNAPSHOT notation for the versions, preferring instead to just set the version ahead.

1. `git checkout main`
2. `git merge development`
3. `mvn clean install`
4. Checkin all changes (if any)
5. `git tag -a 'Release-$version' -m 'Release-$version'`
6. `git push`
7. `mvn deploy`
8. `git checkout development && git merge main`
9. Update version in POM file
10. `mvn clean install`
11. Checkin all changes

## Results
Check at https://central.sonatype.com/publishing/deployments to see the publication process
