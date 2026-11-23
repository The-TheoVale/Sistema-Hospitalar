
package clinica.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class Conexao {

    private static final Properties propriedades = new Properties();

    // Carrega as configurações do arquivo .env
    static {
        try {
            Path caminho = Path.of(".env");

            if (!Files.exists(caminho)) {
                throw new IOException("Arquivo .env não encontrado.");
            }

            propriedades.load(
                Files.newBufferedReader(
                    caminho,
                    StandardCharsets.UTF_8
                )
            );

        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // Estabelece a conexão com o banco Neon
    public static Connection conectar() throws SQLException {

        String host = propriedades.getProperty("NEON_HOST");
        String database = propriedades.getProperty("NEON_DATABASE");
        String user = propriedades.getProperty("NEON_USER");
        String password = propriedades.getProperty("NEON_PASSWORD");

        if (host == null || database == null ||
            user == null || password == null) {

            throw new IllegalStateException(
                "Credenciais da Neon não configuradas corretamente."
            );
        }

        String url = "jdbc:postgresql://" + host +
                     ":5432/" + database +
                     "?sslmode=require";

        return DriverManager.getConnection(
            url, user, password
        );
    }
}
