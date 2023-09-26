package org.example;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;

import static org.neo4j.driver.Values.parameters;
public class HelloWorldExample implements AutoCloseable{
    private final Driver driver;

    public HelloWorldExample(String uri, String user, String password){
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }
    public void printGreeting(final String message) {
        try (var session = driver.session()) {
            var greeting = session.executeWrite(tx -> {
                var query = new Query("CREATE (a:Greeting) SET a.message = $message RETURN a.message + ', from node ' + id(a)", parameters("message", message));
                var result = tx.run(query);
                return result.single().get(0).asString();
            });
            System.out.println(greeting);
            this.addPerson("Alice");
        }
    }

    public static void main(String... args) {
        try (var greeter = new HelloWorldExample("neo4j+s://01b250f6.databases.neo4j.io", "neo4j", "RnU7N0ZK_O0rCCcW-Ta7KbgsZK3RPUIVnFngcOU7120")) {
            greeter.printGreeting("hello, world");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void close() throws Exception{
        driver.close();
    }
    public void addPerson(final String name) {
        try (var session = driver.session()) {
            session.executeWriteWithoutResult(tx -> tx.run("CREATE (a:Person {name: $name})", parameters("name", name)).consume());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
