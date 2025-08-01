# --- Estágio de Build ---
# Começamos com uma imagem base que tem o JDK 21 e o Gradle para compilar nosso projeto.
FROM gradle:8.5-jdk21-focal AS build

# Define o diretório de trabalho dentro do container.
WORKDIR /app

# Copia os arquivos de configuração do Gradle primeiro.
# O Docker faz cache, então se esses arquivos não mudarem, ele não baixa as dependências de novo.
COPY build.gradle settings.gradle ./

# Copia o resto do código-fonte.
COPY src ./src

# Comando para o Gradle compilar o projeto e gerar o arquivo .jar executável.
# O --no-daemon usa menos memória, ideal para ambientes de build.
RUN gradle build --no-daemon


# --- Estágio de Produção ---
# Agora, começamos uma nova imagem, muito menor, apenas com o Java necessário para rodar.
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho.
WORKDIR /app

# Copia apenas o arquivo .jar gerado no estágio de build para a imagem final.
# Isso torna nossa imagem final muito menor e mais segura.
COPY --from=build /app/build/libs/*.jar app.jar

# Expõe a porta 8080, que é a porta padrão do Spring Boot.
EXPOSE 8080

# Comando que será executado quando o container iniciar.
# Ele simplesmente executa nossa aplicação Spring Boot.
ENTRYPOINT ["java", "-jar", "app.jar"]