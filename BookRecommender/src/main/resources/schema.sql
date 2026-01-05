CREATE TABLE IF NOT EXISTS utenti_registrati(
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cognome VARCHAR(255) NOT NULL,
    email VARCHAR(128) UNIQUE NOT NULL,
    password_hash VARCHAR(256) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS libri(
    id SERIAL PRIMARY KEY,
    titolo TEXT NOT NULL,
    autore TEXT NOT NULL,
    anno INT,
    genere TEXT,
    descrizione TEXT
);

CREATE TABLE IF NOT EXISTS librerie(
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES utenti_registrati(id) ON DELETE CASCADE,
    nome VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS libreria_libri(
    libreria_id INT REFERENCES librerie(id) ON DELETE CASCADE,
    libro_id INT REFERENCES libri(id) ON DELETE CASCADE,
    PRIMARY KEY(libreria_id, libro_id)
);

CREATE TABLE IF NOT EXISTS valutazioni_libri(
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES utenti_registrati(id) ON DELETE CASCADE,
    libro_id INT REFERENCES libri(id) ON DELETE CASCADE,
    voto INT CHECK (voto BETWEEN 1 AND 5),
    commento TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, libro_id)
);

CREATE TABLE IF NOT EXISTS consigli_libri(
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES utenti_registrati(id) ON DELETE CASCADE,
    libro_id INT REFERENCES libri(id) ON DELETE CASCADE,
    motivo TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);