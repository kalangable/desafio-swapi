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

### Adicionando uma nova fonte

1. Criar o package `source/<nome>/` com um `@ConfigurationProperties(prefix = "sources.<nome>")`.
2. Criar o parser do JSON daquela API, que devolve `List<Film>`.
3. Criar a classe `@Component` que implementa `FilmSource`, com o seu próprio `WebClient`
   (é nele que entra a autenticação) e usando o `HttpFetcher` para a chamada.
4. Configurar o host em `application.properties` (segredos via variável de ambiente).

O `HttpFetcher` é compartilhado e aplica timeout e retry, configurados em `http-client.*`.
