package com.bookrecommender.common;

/**
 * Tipi di richieste supportate dal protocollo.
 *
 * Nota: contiene sia le operazioni usate dalla GUI (LOGIN, REGISTER, SEARCH_BOOKS, RATE_BOOK, PING)
 * sia quelle nominate nelle specifiche (CERCA_TITOLO, VISUALIZZA_LIBRO, etc.) in modo che
 * il server possa rispondere a entrambi i set di comandi.
 */
public enum RequestType {
    // Operazioni base usate dalla GUI esistente
    PING,
    LOGIN,
    REGISTER,
    SEARCH_BOOKS,
    SEARCH_BOOKS_ADVANCED,
    RATE_BOOK,
    BOOK_STATS_AND_REVIEWS,

    // Operazioni nominate nelle specifiche (possono essere usate dai test automatici)
    CERCA_TITOLO,
    CERCA_AUTORE,
    CERCA_AUTORE_ANNO,
    VISUALIZZA_LIBRO,
    REGISTRAZIONE,
    REGISTRA_LIBRERIA,
    AGGIUNGI_LIBRI_A_LIBRERIA,
    LISTA_LIBRERIE,
    LIBRI_IN_LIBRERIA,
    INSERISCI_VALUTAZIONE_LIBRO,
    INSERISCI_SUGGERIMENTO_LIBRO
}
