# Release Process
NOTE: The POM structure uses a property to define the version and this does not work correctly with the `mvn release` process. DO NOT USE IT.

NOTE: we are not using the typical SNAPSHOT notation for the versions, preferring instead to just set the version ahead.

1. Merge development to main (via Pull Request)
2. `git checkout main`
3. `mvn clean install`
4. Checkin all changes 
5. `git tag -a 'Release-$version' -m 'Release-$version'`
6. `mvn deploy`
7. `git checkout development && git merge main`
8. Update version in POM file
9. `mvn clean install`
10. Checkin all changes
