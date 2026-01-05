# MANUALE UTENTE - BookRecommender
## Sistema di Raccomandazione e Valutazione Libri

**Versione:** 1.0.1  
**Data:** 30 Dicembre 2025  
**Autore:** BookRecommender Team

---

## Indice
1. [Introduzione](#1-introduzione)
2. [Requisiti di Sistema](#2-requisiti-di-sistema)
3. [Avvio dell'Applicazione](#3-avvio-dellapplicazione)
4. [Prima Configurazione](#4-prima-configurazione)
5. [Registrazione Nuovo Utente](#5-registrazione-nuovo-utente)
6. [Accesso al Sistema (Login)](#6-accesso-al-sistema-login)
7. [Interfaccia Principale](#7-interfaccia-principale)
8. [Ricerca Libri](#8-ricerca-libri)
9. [Visualizzazione Dettagli Libro](#9-visualizzazione-dettagli-libro)
10. [Valutazione Libri](#10-valutazione-libri)
11. [Suggerimenti Libri Correlati](#11-suggerimenti-libri-correlati)
12. [Gestione Librerie Personali](#12-gestione-librerie-personali)
13. [Funzionalità Avanzate](#13-funzionalità-avanzate)
14. [Risoluzione Problemi](#14-risoluzione-problemi)
15. [Domande Frequenti (FAQ)](#15-domande-frequenti-faq)

---

## 1. Introduzione

### 1.1 Cos'è BookRecommender?

**BookRecommender** è un sistema completo per la gestione, valutazione e raccomandazione di libri che permette agli utenti di:

- 📚 **Esplorare** un vasto catalogo di libri
- 🔍 **Cercare** libri per titolo, autore o anno di pubblicazione
- ⭐ **Valutare** libri secondo 5 criteri specifici
- 💡 **Ricevere** suggerimenti personalizzati
- 📖 **Organizzare** la propria collezione in librerie tematiche
- 👥 **Condividere** recensioni e consigli di lettura

### 1.2 Architettura del Sistema

L'applicazione è composta da due moduli:

- **Server (serverBR)**: Gestisce il database PostgreSQL, elabora le richieste e coordina le operazioni
- **Client (clientBR)**: Interfaccia grafica JavaFX intuitiva per l'interazione con l'utente

### 1.3 Chi può usare BookRecommender?

**Tutti gli utenti** possono:
- Cercare libri nel catalogo
- Visualizzare informazioni dettagliate sui libri
- Consultare valutazioni e suggerimenti degli utenti registrati

**Utenti registrati** possono inoltre:
- Creare librerie personali
- Valutare libri con criteri multipli
- Suggerire libri correlati
- Gestire la propria collezione

---

## 2. Requisiti di Sistema

### 2.1 Requisiti Software

- **Sistema Operativo**: Windows 10/11, Linux, macOS
- **Java Runtime Environment (JRE)**: versione 17 o superiore
- **PostgreSQL**: versione 12 o superiore (solo per il server)
- **Memoria RAM**: minimo 2 GB
- **Spazio su disco**: 500 MB disponibili

### 2.2 Connettività

- Connessione di rete attiva tra client e server
- Porta 5555 (TCP) disponibile per la comunicazione
- Porta 5432 (TCP) per PostgreSQL

---

## 3. Avvio dell'Applicazione

### 3.1 Avvio del Server

**Windows (PowerShell):**
```powershell
cd C:\percorso\BookRecommender
.\run-server.ps1
```

**Linux/macOS:**
```bash
cd /percorso/BookRecommender
./run-server.sh
```

**Output atteso:**
```
Starting server from serverBR/target/serverBR-1.0.0-jar-with-dependencies.jar
ServerBR in ascolto sulla porta 5555
```

> ℹ️ **Nota**: Il server deve essere avviato **prima** del client.

### 3.2 Avvio del Client

**Windows (PowerShell):**
```powershell
cd C:\percorso\BookRecommender
.\run-client.ps1
```

**Linux/macOS:**
```bash
cd /percorso/BookRecommender
./run-client.sh
```

> ✅ **Successo**: Si aprirà automaticamente la finestra dell'applicazione.

---

## 4. Prima Configurazione

### 4.1 Connessione al Server

Il client si connette automaticamente al server utilizzando le impostazioni predefinite:

- **Host**: 127.0.0.1 (localhost)
- **Porta**: 5555

**Per modificare le impostazioni di connessione**, editare il file:
```
clientBR/src/main/resources/app.properties
```

```properties
server.host=127.0.0.1
server.port=5555
```

### 4.2 Verifica Connessione

All'avvio, il client tenta automaticamente di connettersi al server. Se la connessione ha successo, vedrai la schermata principale. In caso contrario, apparirà un messaggio di errore.

---

## 5. Registrazione Nuovo Utente

### 5.1 Accesso alla Registrazione

1. Dalla schermata principale, cliccare sul pulsante **"Login/Registrati"**
2. Si aprirà una finestra di dialogo
3. Cliccare sul pulsante **"Vai a Registrazione"**

### 5.2 Compilazione Campi Obbligatori

La registrazione richiede **6 campi obbligatori**:

#### Campo 1: Nome
- **Descrizione**: Il tuo nome proprio
- **Esempio**: Mario
- **Vincoli**: Campo obbligatorio

#### Campo 2: Cognome  
- **Descrizione**: Il tuo cognome
- **Esempio**: Rossi
- **Vincoli**: Campo obbligatorio

#### Campo 3: Codice Fiscale
- **Descrizione**: Codice fiscale italiano
- **Esempio**: RSSMRA80A01H501Z
- **Vincoli**: 
  - Obbligatorio
  - Esattamente 16 caratteri
  - Deve essere univoco (non può essere già registrato)
  
> ⚠️ **Importante**: Il codice fiscale deve essere di **esattamente 16 caratteri**. Se inferiore o superiore, la registrazione verrà rifiutata.

#### Campo 4: Email
- **Descrizione**: Indirizzo di posta elettronica
- **Esempio**: mario.rossi@email.com
- **Vincoli**:
  - Obbligatorio
  - Deve essere univoco
  - Formato email valido

#### Campo 5: UserID
- **Descrizione**: Nome utente per il login
- **Esempio**: mario.rossi oppure mrossi80
- **Vincoli**:
  - Obbligatorio
  - Deve essere univoco
  - Verrà usato per accedere al sistema
  
> 💡 **Consiglio**: Scegli un UserID facile da ricordare ma unico.

#### Campo 6: Password
- **Descrizione**: Password per proteggere l'account
- **Esempio**: ••••••••
- **Vincoli**:
  - Obbligatorio
  - Case-sensitive (distingue maiuscole/minuscole)
  
> 🔒 **Sicurezza**: Usa una password robusta con lettere, numeri e caratteri speciali.

### 5.3 Conferma Registrazione

1. Verificare che tutti i campi siano compilati correttamente
2. Cliccare sul pulsante **"OK"**
3. Se la registrazione ha successo, apparirà il messaggio:
   ```
   Registrazione completata! Accedi con le tue credenziali.
   ```
4. La finestra tornerà alla modalità login

### 5.4 Possibili Errori in Registrazione

| Errore | Causa | Soluzione |
|--------|-------|-----------|
| "Tutti i campi sono obbligatori" | Uno o più campi vuoti | Compilare tutti i campi |
| "Il codice fiscale deve essere di 16 caratteri" | Codice fiscale non valido | Verificare di inserire esattamente 16 caratteri |
| "Email già registrata" | Email già in uso | Usare un'altra email |
| "UserID già registrato" | UserID già in uso | Scegliere un altro UserID |
| "Codice fiscale già registrato" | CF già in uso | Verificare i dati inseriti |

---

## 6. Accesso al Sistema (Login)

### 6.1 Procedura di Login

1. Dalla schermata principale, cliccare **"Login/Registrati"**
2. Assicurarsi di essere in modalità **"Login"** (se non lo sei, cliccare "Vai a Login")
3. Inserire le credenziali:
   - **UserID**: Il nome utente scelto durante la registrazione
   - **Password**: La password scelta durante la registrazione
4. Cliccare **"OK"**

### 6.2 Login Riuscito

Se le credenziali sono corrette:
- Apparirà il messaggio: **"Ciao [Nome]!"**
- La barra di stato mostrerà il tuo nome
- Il pulsante **"Gestisci Librerie"** diventerà visibile
- Potrai accedere a tutte le funzionalità riservate

### 6.3 Errori di Login

| Errore | Causa | Soluzione |
|--------|-------|-----------|
| "Credenziali non valide" | UserID o password errati | Verificare UserID e password |
| "Errore di comunicazione" | Server non disponibile | Verificare che il server sia avviato |

> ⚠️ **Attenzione**: La password è case-sensitive. Verificare maiuscole e minuscole.

---

## 7. Interfaccia Principale

### 7.1 Layout dell'Applicazione

L'interfaccia è divisa in **4 sezioni principali**:

```
┌─────────────────────────────────────────────────────┐
│            BARRA SUPERIORE                          │
│  [Login/Registrati]          Utente: (Nome Utente)  │
├─────────────────────────────────────────────────────┤
│            SEZIONE RICERCA                          │
│  Autore: [_____]  Titolo: [_____]  Anno: [____]    │
│  [Cerca]                                            │
├─────────────────────────────────────────────────────┤
│            RISULTATI RICERCA                        │
│  ┌───────────────────────────────────────────────┐ │
│  │  Risultato 1                                  │ │
│  │  Risultato 2                                  │ │
│  │  ...                                          │ │
│  └───────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────┤
│            AZIONI                                   │
│  [Gestisci Librerie]  [Aggiungi Libro]             │
└─────────────────────────────────────────────────────┘
```

### 7.2 Elementi dell'Interfaccia

#### Barra Superiore
- **Pulsante "Login/Registrati"**: Accesso al sistema
- **Etichetta Utente**: Mostra il nome dell'utente autenticato (es. "Utente: Mario")

#### Sezione Ricerca
- **Campo Autore**: Filtra per nome dell'autore
- **Campo Titolo**: Filtra per titolo del libro
- **Campo Anno**: Filtra per anno di pubblicazione
- **Pulsante "Cerca"**: Esegue la ricerca

#### Area Risultati
- **Lista Libri**: Visualizza i risultati della ricerca
- **Doppio-click**: Apre i dettagli del libro selezionato

#### Barra Azioni (visibile solo per utenti autenticati)
- **"Gestisci Librerie"**: Accede alla gestione delle librerie personali
- **"Aggiungi Libro"**: Aggiunge un nuovo libro al catalogo (se abilitato)

---

## 8. Ricerca Libri

### 8.1 Ricerca Semplice

**Ricerca per un solo criterio:**

1. Inserire il criterio di ricerca in uno dei campi:
   - **Autore**: es. "Dante Alighieri"
   - **Titolo**: es. "Divina Commedia"
   - **Anno**: es. "1321"
2. Lasciare gli altri campi vuoti
3. Cliccare **"Cerca"**

### 8.2 Ricerca Combinata

**Ricerca con più criteri contemporaneamente:**

1. Compilare più campi contemporaneamente:
   - Esempio: Autore = "Dante" AND Anno = "1321"
2. Il sistema mostrerà solo i libri che soddisfano **tutti** i criteri inseriti
3. Cliccare **"Cerca"**

### 8.3 Ricerca Parziale

Il sistema supporta la **ricerca parziale**:
- "Dante" trova "Dante Alighieri"
- "Divina" trova "Divina Commedia"
- La ricerca **non è case-sensitive** (maiuscole/minuscole sono ignorate)

### 8.4 Visualizzazione Risultati

Per ogni libro trovato, viene mostrato:
- **Titolo** del libro
- **Autore**
- **Anno di pubblicazione**
- **Genere**
- **Valutazione media** (se presente)

### 8.5 Azioni sui Risultati

- **Click singolo**: Seleziona il libro
- **Doppio-click**: Apre la finestra dei dettagli completi

---

## 9. Visualizzazione Dettagli Libro

### 9.1 Apertura Dettagli

1. Dalla lista risultati, fare **doppio-click** su un libro
2. Si aprirà una nuova finestra con i dettagli completi

### 9.2 Informazioni Visualizzate

#### Sezione "Informazioni Generali"
- **Titolo**: Titolo completo del libro
- **Autore**: Nome dell'autore
- **Anno di Pubblicazione**: Anno di prima pubblicazione
- **Genere**: Categoria del libro (Fantasy, Romanzo, Saggistica, ecc.)
- **Sottogenere**: Sottocategoria più specifica
- **Editore**: Casa editrice
- **ISBN**: Codice identificativo internazionale

#### Sezione "Statistiche e Valutazioni"
- **Numero di Pagine**: Lunghezza del libro
- **Numero di Valutazioni**: Quanti utenti hanno valutato il libro
- **Valutazione Media**: Media di tutte le valutazioni (0-10)

### 9.3 Tabs Aggiuntivi

#### Tab "Recensioni"
Visualizza tutte le valutazioni degli utenti con:
- Nome dell'utente che ha valutato
- Voto finale (0-10)
- Note testuali (max 256 caratteri)
- Data della valutazione

#### Tab "Suggerimenti"
Mostra i libri correlati suggeriti dagli utenti:
- Titolo del libro suggerito
- Motivazione del suggerimento
- Nome dell'utente che ha suggerito

---

## 10. Valutazione Libri

### 10.1 Accesso alla Valutazione

> ⚠️ **Prerequisito**: Devi essere **autenticato** per valutare un libro.

1. Aprire i dettagli di un libro
2. Cliccare sul pulsante **"Valuta questo libro"**
3. Si aprirà la finestra di valutazione

### 10.2 Criteri di Valutazione

BookRecommender utilizza **5 criteri di valutazione**, ognuno con punteggio **0-2**:

#### 1. Stile di Scrittura
- **0**: Molto scarso, illeggibile
- **1**: Accettabile, ma migliorabile
- **2**: Eccellente, coinvolgente

#### 2. Contenuto
- **0**: Noioso, poco interessante
- **1**: Discreto
- **2**: Avvincente, ben strutturato

#### 3. Gradevolezza
- **0**: Non piacevole
- **1**: Abbastanza piacevole
- **2**: Molto piacevole

#### 4. Originalità
- **0**: Ripetitivo, banale
- **1**: Alcune idee originali
- **2**: Molto originale e innovativo

#### 5. Edizione
- **0**: Scadente (errori, impaginazione)
- **1**: Buona
- **2**: Eccellente (carta pregiata, rilegatura, ecc.)

### 10.3 Calcolo del Voto Finale

Il **voto finale** (0-10) è calcolato automaticamente dal database:

```
Voto Finale = ROUND((stile + contenuto + gradevolezza + originalità + edizione) * 10.0 / 10.0, 1)
```

**Esempio:**
- Stile: 2
- Contenuto: 2
- Gradevolezza: 1
- Originalità: 2
- Edizione: 1

**Totale**: 8 → **Voto Finale**: 8.0

### 10.4 Aggiungere Note alla Valutazione

1. Nel campo **"Note"** puoi aggiungere un commento testuale
2. **Limite**: 256 caratteri massimo
3. Le note sono **facoltative** ma consigliate per motivare il voto

**Esempio di nota:**
```
"Un capolavoro della letteratura italiana. Lo stile è ricercato ma accessibile. 
Consigliato a tutti gli appassionati di classici."
```

### 10.5 Conferma Valutazione

1. Verificare tutti i punteggi inseriti
2. Controllare le note (se presenti)
3. Cliccare **"Conferma Valutazione"**
4. Messaggio di conferma: **"Valutazione salvata con successo!"**

### 10.6 Modifica Valutazione Esistente

- Se hai già valutato un libro, **puoi modificare** la valutazione
- Aprire i dettagli e cliccare nuovamente **"Valuta questo libro"**
- I campi saranno precompilati con i valori precedenti
- Modificare e salvare

---

## 11. Suggerimenti Libri Correlati

### 11.1 Cosa sono i Suggerimenti?

I suggerimenti permettono di creare **collegamenti semantici** tra libri correlati:
- Libri dello stesso autore
- Libri dello stesso genere
- Libri con tematiche simili
- Sequel o prequel

### 11.2 Aggiungere un Suggerimento

> ⚠️ **Prerequisito**: Devi essere **autenticato**.

1. Aprire i dettagli del libro **di partenza**
2. Cliccare sul pulsante **"Suggerisci libro correlato"**
3. Si aprirà una finestra di ricerca

### 11.3 Ricerca del Libro Correlato

1. Utilizzare i campi di ricerca per trovare il libro da suggerire
2. Cliccare **"Cerca"**
3. Selezionare il libro dalla lista dei risultati
4. Inserire la **motivazione** del suggerimento (max 500 caratteri)

**Esempio di motivazione:**
```
"Stesso autore, ambientazione simile. Consigliato se ti è piaciuto questo libro."
```

### 11.4 Conferma Suggerimento

1. Cliccare **"Conferma Suggerimento"**
2. Il suggerimento verrà salvato nel sistema
3. Sarà visibile a tutti gli utenti nella tab "Suggerimenti" del libro

### 11.5 Visualizzare Suggerimenti Esistenti

1. Aprire i dettagli di un libro
2. Cliccare sul tab **"Suggerimenti"**
3. Verranno mostrati tutti i suggerimenti creati dagli utenti con:
   - Titolo del libro suggerito
   - Motivazione
   - Nome dell'utente che ha suggerito

---

## 12. Gestione Librerie Personali

### 12.1 Cosa sono le Librerie?

Le **librerie personali** permettono di organizzare i tuoi libri in collezioni tematiche:
- "Libri da leggere"
- "Preferiti"
- "Romanzi storici"
- "Classici"
- ecc.

### 12.2 Accesso alla Gestione Librerie

> ⚠️ **Prerequisito**: Devi essere **autenticato**.

1. Dalla schermata principale, cliccare **"Gestisci Librerie"**
2. Si aprirà la finestra di gestione

### 12.3 Creare una Nuova Libreria

1. Cliccare sul pulsante **"Crea Nuova Libreria"**
2. Inserire il **nome** della libreria
   - Esempio: "Fantascienza"
   - Limite: 100 caratteri
3. Cliccare **"OK"**
4. La libreria verrà creata e mostrata nella lista

### 12.4 Aggiungere Libri a una Libreria

1. Selezionare una libreria dalla lista
2. Cliccare **"Aggiungi Libro"**
3. Cercare il libro desiderato utilizzando i filtri
4. Selezionare il libro dalla lista risultati
5. Cliccare **"Aggiungi alla libreria selezionata"**
6. Il libro verrà aggiunto alla libreria

### 12.5 Visualizzare Contenuto Libreria

1. Selezionare una libreria dalla lista
2. I libri contenuti verranno mostrati nella sezione inferiore con:
   - Titolo
   - Autore
   - Anno

### 12.6 Rimuovere Libri da una Libreria

1. Selezionare la libreria
2. Selezionare il libro da rimuovere dalla lista
3. Cliccare **"Rimuovi libro dalla libreria"**
4. Confermare la rimozione

### 12.7 Eliminare una Libreria

1. Selezionare la libreria da eliminare
2. Cliccare **"Elimina Libreria"**
3. Confermare l'eliminazione
4. **Attenzione**: Verranno rimossi anche tutti i libri contenuti

---

## 13. Funzionalità Avanzate

### 13.1 Aggiungere Nuovi Libri al Catalogo

Se abilitato dall'amministratore:

1. Cliccare **"Aggiungi Libro"** dalla schermata principale
2. Compilare tutti i campi obbligatori:
   - ISBN (univoco)
   - Titolo
   - Autore
   - Anno di pubblicazione
   - Genere
   - Editore
   - Numero di pagine
3. Cliccare **"Salva"**

### 13.2 Statistiche Personali

Nel pannello utente (visibile dopo il login) puoi visualizzare:
- Numero di valutazioni effettuate
- Numero di suggerimenti creati
- Numero di librerie personali
- Ultimo libro valutato

### 13.3 Filtri Avanzati

Nella sezione "Gestisci Librerie" puoi filtrare i libri per:
- Genere
- Range di anni
- Numero minimo di pagine
- Valutazione minima

---

## 14. Risoluzione Problemi

### 14.1 Problemi di Connessione

**Sintomo**: "Errore di comunicazione con il server"

**Soluzioni**:
1. Verificare che il server sia avviato:
   ```powershell
   .\run-server.ps1
   ```
2. Controllare che la porta 5555 non sia bloccata dal firewall
3. Verificare le impostazioni in `app.properties`

### 14.2 Login Non Funzionante

**Sintomo**: "Credenziali non valide" nonostante i dati corretti

**Soluzioni**:
1. Verificare che il **UserID** sia corretto (non l'email)
2. Controllare maiuscole/minuscole nella password
3. Provare a registrarsi nuovamente se il problema persiste

### 14.3 Valutazione Non Salvata

**Sintomo**: La valutazione non viene salvata

**Soluzioni**:
1. Verificare di essere autenticato
2. Controllare che tutti i criteri siano selezionati (0-2)
3. Le note non devono superare 256 caratteri
4. Verificare la connessione al server

### 14.4 Librerie Non Visualizzate

**Sintomo**: Le librerie create non appaiono

**Soluzioni**:
1. Effettuare il logout e login nuovamente
2. Verificare la connessione al database sul server
3. Controllare i log del server per errori

### 14.5 Interfaccia Grafica Bloccata

**Sintomo**: L'applicazione non risponde

**Soluzioni**:
1. Chiudere e riaprire il client
2. Verificare che il server sia attivo e risponda
3. Controllare i log del client per eccezioni

---

## 15. Domande Frequenti (FAQ)

### Q1: Posso cercare libri senza registrarmi?
**R**: Sì, la ricerca e la visualizzazione dei dettagli sono disponibili per tutti. Solo valutazione, suggerimenti e librerie richiedono la registrazione.

### Q2: Come posso cambiare la password?
**R**: Attualmente la modifica password non è disponibile. Contattare l'amministratore del sistema.

### Q3: Posso eliminare una valutazione?
**R**: No, ma puoi modificarla aprendo nuovamente la finestra di valutazione.

### Q4: Quante librerie posso creare?
**R**: Non c'è un limite predefinito. Puoi creare tutte le librerie che desideri.

### Q5: Posso condividere le mie librerie con altri utenti?
**R**: Attualmente le librerie sono private. La condivisione potrebbe essere aggiunta in future versioni.

### Q6: Il voto finale è una media dei criteri?
**R**: Sì, è la somma dei 5 criteri (max 10) normalizzata in scala 0-10 e arrotondata a un decimale.

### Q7: Posso suggerire lo stesso libro più volte?
**R**: No, ogni utente può suggerire un libro correlato solo una volta per ogni libro di partenza.

### Q8: Le note nelle valutazioni sono obbligatorie?
**R**: No, le note sono facoltative, ma fortemente consigliate per motivare il voto.

### Q9: Cosa succede se inserisco un codice fiscale errato?
**R**: La registrazione verrà rifiutata se il codice fiscale non ha esattamente 16 caratteri o se è già stato usato.

### Q10: Posso usare l'email per il login?
**R**: No, il login richiede il **UserID** scelto durante la registrazione, non l'email.

---

## Supporto e Contatti

Per assistenza tecnica o segnalazione bug:
- **Email**: support@bookrecommender.com
- **GitHub Issues**: https://github.com/bookrecommender/issues
- **Documentazione**: https://docs.bookrecommender.com

---

**© 2025 BookRecommender Team - Tutti i diritti riservati**

---

## Avvio dell'Applicazione

### 1. Avvio del Server

**Windows (PowerShell):**
```powershell
.\run-server.ps1
```

Il server:
- Si avvia sulla porta **5555** (default)
- Crea automaticamente lo schema del database
- Importa i dati iniziali se il database è vuoto
- Resta in ascolto delle connessioni client

**Messaggio di conferma:**
```
Server BookRecommender avviato su porta 5555
Database inizializzato correttamente
```

### 2. Avvio del Client

**Windows (PowerShell):**
```powershell
.\run-client.ps1
```

Il client si connette automaticamente al server e mostra la finestra di login.

---

## Registrazione e Login

### Registrazione Nuovo Utente

1. Nella schermata iniziale, cliccare sul pulsante **"Registrati"**
2. Compilare i campi richiesti:
   - **Nome**: Il tuo nome
   - **Cognome**: Il tuo cognome
   - **Codice Fiscale**: Codice fiscale (16 caratteri)
   - **Email**: Indirizzo email (deve essere unico)
   - **UserID**: Nome utente per il login (deve essere unico)
   - **Password**: Password di accesso
3. Cliccare **"Conferma Registrazione"**
4. Se la registrazione ha successo, verrai reindirizzato alla schermata di login

**Note:**
- L'email e l'UserID devono essere unici nel sistema
- Il codice fiscale deve essere di esattamente 16 caratteri
- Tutti i campi sono obbligatori

### Login

1. Inserire **UserID** e **Password** nei campi appositi
2. Cliccare **"Login"**
3. Se le credenziali sono corrette, accederai all'applicazione

**In caso di errore:**
- Verificare che UserID e password siano corretti
- Assicurarsi di essere registrati nel sistema
- Le password sono case-sensitive

---

## Ricerca Libri

L'applicazione offre due modalità di ricerca: **Ricerca Semplice** e **Ricerca Avanzata**.

### Ricerca Semplice

1. Dalla schermata principale, selezionare il tab **"Ricerca"**
2. Inserire il termine di ricerca nel campo di testo
3. Cliccare **"Cerca"**
4. I risultati appariranno nella tabella sottostante

**La ricerca semplice cerca per:**
- Titolo del libro (case-insensitive)
- Ricerca parziale (substring matching)

### Ricerca Avanzata

1. Selezionare il tab **"Ricerca Avanzata"**
2. Scegliere il tipo di ricerca:

#### a) Ricerca per Titolo
- Inserire il titolo (o parte di esso) nel campo **"Titolo"**
- Cliccare **"Cerca Titolo"**

#### b) Ricerca per Autore
- Inserire il nome dell'autore nel campo **"Autore"**
- Cliccare **"Cerca Autore"**

#### c) Ricerca per Autore e Anno
- Inserire sia **"Autore"** che **"Anno"**
- Cliccare **"Cerca Autore + Anno"**
- Trova libri dell'autore specificato pubblicati nell'anno indicato

### Visualizzazione Risultati

La tabella dei risultati mostra:
- **Titolo**: Titolo completo del libro
- **Autore**: Nome dell'autore
- **Anno**: Anno di pubblicazione
- **Genere**: Categoria del libro
- **Descrizione**: Breve descrizione (troncata)

**Per visualizzare i dettagli completi:**
- Fare **doppio click** su un libro nella tabella, oppure
- Selezionare un libro e cliccare **"Apri Dettagli"**

---

## Dettagli Libro e Recensioni

### Apertura Dettagli

Dalla tabella dei risultati di ricerca, aprire i dettagli di un libro come descritto sopra.

### Informazioni Visualizzate

La finestra dettagli mostra:

**Sezione Informazioni Libro:**
- Titolo completo
- Autore
- Anno di pubblicazione
- Genere letterario
- Descrizione completa

**Sezione Statistiche:**
- **Media Voti**: Media delle valutazioni (scala 1-5)
- **Numero Recensioni**: Totale recensioni ricevute

**Sezione Recensioni:**
- Lista delle recensioni testuali lasciate dagli utenti
- Ogni recensione mostra il commento completo

---

## Valutazione Libri

### Sistema di Valutazione a 5 Criteri

BookRecommender utilizza un sistema di valutazione avanzato basato su **5 criteri distinti**.

### Come Valutare un Libro

1. Aprire i **Dettagli** del libro
2. Espandere la sezione **"Valutazione libro (5 criteri)"**
3. Regolare i 5 slider per valutare:

   - **Stile**: Qualità della scrittura (1-5)
   - **Contenuto**: Qualità del contenuto e della trama (1-5)
   - **Gradevolezza**: Quanto è piacevole la lettura (1-5)
   - **Originalità**: Quanto è innovativo (1-5)
   - **Edizione**: Qualità dell'edizione fisica/digitale (1-5)

4. **(Facoltativo)** Aggiungere **Note** nel campo di testo
5. Cliccare **"Salva Valutazione"**

**Note:**
- Ogni slider va da 1 (minimo) a 5 (massimo)
- I valori si aggiornano in tempo reale accanto allo slider
- Le note sono opzionali ma raccomandate per recensioni più dettagliate
- Dopo il salvataggio, le statistiche del libro vengono aggiornate automaticamente

---

## Suggerimenti Libri

### Suggerire Libri Correlati

Puoi suggerire fino a **3 libri correlati** per ogni libro che consulti.

### Procedura

1. Dalla finestra **Dettagli Libro**, cliccare **"Suggerisci Libri"**
2. Si aprirà una finestra di dialogo con:
   - **Informazioni del libro selezionato** (sezione espandibile in alto)
   - **3 ComboBox** per selezionare i suggerimenti
   - **Campo di ricerca** per filtrare i libri
   - **Tabella** con tutti i libri disponibili

### Selezione dei Suggerimenti

**Metodo 1: Tramite ComboBox**
- Cliccare su una delle 3 ComboBox
- Selezionare un libro dall'elenco a discesa
- Ripetere per massimo 3 suggerimenti

**Metodo 2: Tramite Ricerca**
- Inserire un termine nel campo **"Cerca libri"**
- Cliccare **"Cerca"** o premere Invio
- La tabella si filtrerà mostrando solo i libri corrispondenti
- Selezionare i libri desiderati dalle ComboBox

**Completamento:**
- Cliccare **"Salva Suggerimenti"** per confermare
- Cliccare **"Annulla"** per chiudere senza salvare

**Note:**
- È possibile suggerire 1, 2 o 3 libri (non obbligatorio riempire tutte le ComboBox)
- Non è possibile suggerire lo stesso libro più volte
- I suggerimenti vengono salvati e associati al libro

---

## Gestione Librerie Personali

### Accesso alla Gestione Librerie

1. Dopo aver effettuato il login
2. Nel tab **"Ricerca"**, cliccare il pulsante **"Gestisci Librerie"**
3. Si aprirà la finestra di gestione librerie personali

### Creazione di una Nuova Libreria

**Metodo Rapido:**
1. Inserire il nome della libreria nel campo di testo
2. Cliccare **"Crea Libreria"**

**Metodo Guidato:**
1. Cliccare il pulsante **"Crea Libreria"** (in alto)
2. Inserire il nome nel dialog che appare
3. Confermare

**Note:**
- Il nome della libreria deve essere univoco per il tuo account
- Puoi creare quante librerie desideri

### Visualizzazione Librerie

- Tutte le tue librerie sono elencate nella **ComboBox** in alto
- Selezionare una libreria per visualizzarne i dettagli

### Aggiunta Libri a una Libreria

1. Selezionare la libreria desiderata dalla ComboBox
2. Cliccare **"Aggiungi Libri"**
3. Cercare i libri da aggiungere usando il campo di ricerca
4. Selezionare i libri dalla tabella
5. Confermare l'aggiunta

### Visualizzazione Libri in una Libreria

1. Selezionare la libreria dalla ComboBox
2. Cliccare **"Visualizza Libri in Libreria"**
3. I libri della libreria appariranno nella tabella sottostante

### Ricerca all'interno di una Libreria

1. Visualizzare i libri di una libreria
2. Inserire un termine nel campo **"Cerca nella libreria"**
3. I risultati si filtreranno automaticamente

---

## Risoluzione Problemi

### Il Server non si Avvia

**Problema:** Errore "Porta già in uso"
- **Soluzione:** Un'altra istanza del server è già attiva. Chiuderla prima di riavviare.
- **Alternativa:** Modificare la porta nel file `application.properties`

**Problema:** Errore di connessione al database
- **Soluzione:** 
  1. Verificare che PostgreSQL sia installato e in esecuzione
  2. Verificare le credenziali in `application.properties`:
     ```
     db.url=jdbc:postgresql://localhost:5432/bookrecommender
     db.user=postgres
     db.password=postgres
     ```
  3. Assicurarsi che il database `bookrecommender` esista

### Il Client non si Connette al Server

**Problema:** "Impossibile connettersi al server"
- **Soluzione:**
  1. Verificare che il server sia avviato
  2. Controllare le impostazioni in `clientBR/src/main/resources/app.properties`:
     ```
     server.host=127.0.0.1
     server.port=5555
     ```
  3. Verificare che non ci siano firewall che bloccano la porta 5555

### Errori di Login

**Problema:** "Credenziali non valide"
- **Soluzione:**
  1. Verificare email e password
  2. Controllare di essere registrati (fare nuova registrazione se necessario)
  3. Le password sono case-sensitive

### La Ricerca non Restituisce Risultati

**Problema:** Nessun libro trovato
- **Soluzione:**
  1. Verificare di aver inserito correttamente il termine di ricerca
  2. Provare con termini più generici (es. parte del titolo invece del titolo completo)
  3. La ricerca è case-insensitive ma sensibile agli spazi

### Errore durante il Salvataggio della Valutazione

**Problema:** "Errore nel salvataggio"
- **Soluzione:**
  1. Verificare di aver effettuato il login
  2. Controllare che tutti gli slider siano impostati
  3. Riavviare il client e riprovare

### La Finestra delle Librerie non si Apre

**Problema:** Nessuna reazione al click su "Gestisci Librerie"
- **Soluzione:**
  1. Verificare di aver effettuato il login
  2. Il pulsante è visibile solo dopo il login
  3. Controllare la console per eventuali errori

---

## Suggerimenti per un Utilizzo Ottimale

### Best Practices

1. **Valutazioni Accurate**: Usa tutti e 5 i criteri per valutazioni più complete e utili
2. **Note Dettagliate**: Aggiungi note testuali alle valutazioni per aiutare altri lettori
3. **Suggerimenti Pertinenti**: Suggerisci libri realmente correlati per tema o stile
4. **Organizza le Librerie**: Crea librerie tematiche per organizzare meglio i tuoi libri
5. **Ricerca Avanzata**: Usa la ricerca avanzata per trovare libri specifici più rapidamente

### Scorciatoie

- **Doppio Click**: Sulla tabella per aprire rapidamente i dettagli di un libro
- **Invio**: Nel campo di ricerca per avviare la ricerca
- **Tab**: Per navigare rapidamente tra i campi del form

---

## Supporto

Per problemi tecnici o domande sull'applicazione, verificare:

1. **File di Log**: I log del server e client possono contenere informazioni utili
2. **Connessione Database**: La maggior parte dei problemi deriva da configurazioni errate del DB
3. **Porte di Rete**: Assicurarsi che le porte 5432 (PostgreSQL) e 5555 (Server) siano disponibili

---

## Note Legali

BookRecommender è un'applicazione didattica sviluppata per scopi educativi.
Tutti i diritti sui dati dei libri appartengono ai rispettivi proprietari.

---

**Versione Manuale:** 1.0.0  
**Data:** Dicembre 2025  
**Applicazione:** BookRecommender v1.0.0
