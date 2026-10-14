# desafio-swapi
Desenvolver api usando dados de SWAPI

## Cargas de dados (fontes)

Ao subir a aplicação, os filmes são carregados a partir de uma ou mais **fontes** (`FilmSource`).
Cada fonte conhece a sua API: host, autenticação e formato do JSON. Hoje existe a `SwapiFilmSource`,
mas é possível ter várias cargas: o `FilmDataLoader` percorre todas as fontes registradas e, se uma
falhar, registra o erro e segue para a próxima.

#### Visão geral

```mermaid
flowchart LR
    Loader[FilmDataLoader] --> Source[FilmSource]
    Source --> Repo[FilmCreation]
```

#### Fonte SWAPI

```mermaid
flowchart LR
    Loader[FilmDataLoader] --> Swapi[SwapiFilmSource]
    Swapi --> Fetcher[HttpFetcher]
    Fetcher --> API[(swapi.dev)]
    Swapi --> Parser[SwapiFilmParser]
    Loader --> Repo[FilmCreation]
```

#### Outra fonte (exemplo)

```mermaid
flowchart LR
    Loader[FilmDataLoader] --> Other[OutraFilmSource]
    Other --> Fetcher[HttpFetcher]
    Fetcher --> API[(Outra API<br/>com autenticação)]
    Other --> Parser[OutraFilmParser]
    Loader --> Repo[FilmCreation]
```

#### Sequência da carga

```mermaid
sequenceDiagram
    participant Loader as FilmDataLoader
    participant Source as FilmSource (ex: Swapi)
    participant Fetcher as HttpFetcher
    participant API as API externa
    participant Parser as Parser da fonte
    participant Repo as FilmCreation

    Note over Loader: na inicialização da aplicação
    loop para cada FilmSource
        Loader->>Source: fetch()
        Source->>Fetcher: get(webClient, resource)
        loop timeout / retry
            Fetcher->>API: GET (com autenticação da fonte)
            API-->>Fetcher: JSON
        end
        Fetcher-->>Source: JSON
        Source->>Parser: parse(json)
        Parser-->>Source: List<Film>
        Source-->>Loader: List<Film>
        Loader->>Repo: insert(film)
        Note over Loader: se a fonte falhar, loga o erro e segue para a próxima
    end
```

### O que a fonte define

Cada fonte devolve `List<Film>` já preenchida com o que a sua API oferece:

- `title`, `releaseDate` e, se existirem, `synopsis`, `runtimeMinutes` e `director`;
- `label`: rótulo opcional usado no nome de apresentação (na SWAPI, "Episode IV");
- `extras`: `Map<String, String>` com dados específicos da fonte (na SWAPI, `openingCrawl` e `producer`);
- `source` e `externalId`: de onde o filme veio e o seu identificador lá;
- `id` (`Identifier`): definido pela própria fonte, no parser (na SWAPI, `star-wars-4` para o episódio IV).

Ao carregar, o `FilmDataLoader` não insere filme sem `id` nem com `id` já existente: ele é pulado, a carga segue e o problema aparece como `WARN` no log ao final da fonte.

## Endpoints

| Método | Path | Descrição |
|---|---|---|
| GET | `/films?page=&size=` | Lista paginada com `id` e `displayTitle` (ex.: "Episode IV - A New Hope") |
| GET | `/films/{id}` | Detalhe do filme, incluindo `extras` |
| PATCH | `/films/{id}` | Altera `title`, `releaseDate`, `synopsis`, `runtimeMinutes` e `director` |

O `displayTitle` é derivado: `label - title` quando há `label`, senão só o `title`.
A documentação interativa fica em `/swagger-ui.html`.

### Paginação

`GET /films` devolve uma página no formato:

```json
{
  "content": [
    { "id": "star-wars-1", "displayTitle": "Episode IV - A New Hope" },
    { "id": "star-wars-2", "displayTitle": "Episode V - The Empire Strikes Back" }
  ],
  "page": { "size": 2, "number": 0, "totalElements": 6, "totalPages": 3 }
}
```

- `page` começa em 0 e `size` é a quantidade de filmes por página. Um filme nunca aparece em mais de uma página.
- Uma página depois da última devolve `content` vazio.
- `page` negativo, `size` menor que 1 ou acima do máximo devolve 400.
- Os valores padrão ficam no `application.properties`:

```properties
pagination.default-page=0
pagination.default-size=2
pagination.max-size=100
```

### Adicionando uma nova fonte

1. Criar o package `ingestion/<nome>/` com um `@ConfigurationProperties(prefix = "sources.<nome>")`.
2. Criar o parser do JSON daquela API, que devolve `List<Film>`.
3. Criar a classe `@Component` que implementa `FilmSource`, com o seu próprio `WebClient`
   (é nele que entra a autenticação) e usando o `HttpFetcher` para a chamada.
4. Configurar o host em `application.properties` (segredos via variável de ambiente).

O `HttpFetcher` é compartilhado e aplica timeout e retry, configurados em `http-client.*`.
