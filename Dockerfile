# Imagem da aplicação: só empacota o jar que já foi construído (localmente, no CI ou pipeline).
# Não faz build nem baixa dependências Maven, por isso o build da imagem é rápido.
#
# Como usar:
#   1) Gerar o jar:        mvn clean package -DskipTests
#   2) Construir a imagem: docker build -t desafio-swapi .
#   3) Executar:           docker run --rm -p 8080:8080 desafio-swapi
#
# Logs em JSON (Grafana/Loki): docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=json desafio-swapi
# Se não tiver o Maven ou o JDK na máquina, use o Dockerfile.build (constrói o jar dentro do Docker).
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --no-create-home appuser
COPY target/*.jar app.jar
USER appuser

# A JVM respeita o limite de memória do contêiner; este percentual é sobrescrevível
# no Kubernetes (env JAVA_TOOL_OPTIONS) sem reconstruir a imagem.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
