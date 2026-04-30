package com.fitfind.fitfind;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods=false)
public class TestContainerConfiguration {

    @Bean
    @ServiceConnection(name = "postgress")
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(5432)
            .withCreateContainerCmdModifier(cmd ->
                cmd.withHostConfig(
                    new HostConfig().withPortBindings(
                        new PortBinding(
                            Ports.Binding.bindPortRange(9800, 9876),
                            new ExposedPort(5432)
                        )
                    )
                )
            );
    }
}
