CREATE TABLE IF NOT EXISTS UtentiRegistrati(
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL,
    cognome TEXT NOT NULL,
    codice_fiscale VARCHAR(16) UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    userid TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Libri(
    id SERIAL PRIMARY KEY,
    titolo TEXT NOT NULL,
    autore TEXT NOT NULL,
    anno INT,
    genere TEXT,
    descrizione TEXT
);

CREATE TABLE IF NOT EXISTS Librerie(
    id SERIAL PRIMARY KEY,
    utente_id INT NOT NULL REFERENCES UtentiRegistrati(id) ON DELETE CASCADE,
    nome TEXT NOT NULL,
    UNIQUE(utente_id, nome)
);

CREATE TABLE IF NOT EXISTS Librerie_Libri(
    libreria_id INT NOT NULL REFERENCES Librerie(id) ON DELETE CASCADE,
    libro_id INT NOT NULL REFERENCES Libri(id) ON DELETE CASCADE,
    PRIMARY KEY(libreria_id, libro_id)
);

CREATE TABLE IF NOT EXISTS ValutazioniLibri(
    utente_id INT NOT NULL REFERENCES UtentiRegistrati(id) ON DELETE CASCADE,
    libro_id INT NOT NULL REFERENCES Libri(id) ON DELETE CASCADE,
    stile INT CHECK(stile BETWEEN 1 AND 5),
    contenuto INT CHECK(contenuto BETWEEN 1 AND 5),
    gradevolezza INT CHECK(gradevolezza BETWEEN 1 AND 5),
    originalita INT CHECK(originalita BETWEEN 1 AND 5),
    edizione INT CHECK(edizione BETWEEN 1 AND 5),
    voto_finale NUMERIC(3,2) GENERATED ALWAYS AS (
        ROUND(
            (COALESCE(stile,0)
           + COALESCE(contenuto,0)
           + COALESCE(gradevolezza,0)
           + COALESCE(originalita,0)
           + COALESCE(edizione,0))::numeric / 5.0, 2
        )
    ) STORED,
    note VARCHAR(256),
    PRIMARY KEY(utente_id, libro_id)
);

CREATE TABLE IF NOT EXISTS ConsigliLibri(
    utente_id INT NOT NULL REFERENCES UtentiRegistrati(id) ON DELETE CASCADE,
    libro_id INT NOT NULL REFERENCES Libri(id) ON DELETE CASCADE,
    suggerito_libro_id INT NOT NULL REFERENCES Libri(id) ON DELETE CASCADE,
    PRIMARY KEY(utente_id, libro_id, suggerito_libro_id)
);
