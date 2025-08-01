# --- Estágio de Build ---
# CORREÇÃO: Usando uma tag de imagem válida do Gradle com JDK 21
FROM gradle:8.8-jdk21 AS build

# Define o diretório de trabalho dentro do container.
WORKDIR /app

# Copia os arquivos de configuração do Gradle primeiro para otimizar o cache.
COPY build.gradle settings.gradle ./

# Copia o resto do código-fonte.
COPY src ./src

# Comando para o Gradle compilar o projeto e gerar o arquivo .jar executável.
RUN gradle build --no-daemon


# --- Estágio de Produção ---
# Agora, começamos uma nova imagem, muito menor, apenas com o Java necessário para rodar.
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho.
WORKDIR /app

# Copia apenas o arquivo .jar gerado no estágio de build para a imagem final.
COPY --from=build /app/build/libs/*.jar app.jar

# Expõe a porta 8080, que é a porta padrão do Spring Boot.
EXPOSE 8080

# Comando que será executado quando o container iniciar.
ENTRYPOINT ["java", "-jar", "app.jar"]