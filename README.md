# CSC8104 Quickstart Project

This quickstart project is provided for students on the CSC8104 Enterprise Middleware module and provides a foundation for starting their coursework. Students are expected to download and build their solution within the provided project and should not aim to create a new project from scratch.

Within the project there is an example REST service for creating and storing contacts which can be accessed via the Swagger UI endpoint (http://localhost:8080/q/swagger-ui). It is encouraged that students spend spend time reading through this code to gain a strong understanding of how the project works. Not only this, but students are also encouraged to follow a similar packaging structure.

Students are not required to remove the contact service and can leave this functionality in their submission.

Throughout the coursework specification there are many links to various guides to help you complete the coursework. It is strongly encouraged that you spend time working through these guides before attempting to implement the specification requirements.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.
