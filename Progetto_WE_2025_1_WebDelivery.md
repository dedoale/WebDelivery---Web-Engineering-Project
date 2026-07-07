# Corso di Web Engineering<br/>*Progetti A.A. 2025/2026* - Specifica 1

<section class="specifica">

## Progetto "WebDelivery"
> Versione 1.0

### Premessa

I progetti di fine corso si ispirano sempre ad esigenze
reali, e fanno solitamente riferimento a tipologie di sito già presenti sulla
rete. Nello svolgere il progetto, gli studenti dovranno attenersi alla
specifica data in questo documento, ma potranno raffinarla tramite l'interazione
col docente e l'analisi di siti web analoghi. In ogni caso, la realizzazione
finale dovrà essere completamente originale. Le informazioni pubblicate
dovranno essere sempre ben organizzate ed accessibili, date le varie tipologie
di utenza associate alle applicazioni web pubbliche.  

### Specifiche del Sito

<section class="descrizione">



Il sito *WebDelibery* rappresenta l'interfaccia web di una generica attività di ristorazione che offre servizio di consegna a domicilio.

L'attività disporrà di un *menu* composto da *prodotti* ognuno dotato almeno di un nome, una descrizione, un prezzo e, opzionalmente, una (o più) immagini. Internamente (cioè in modo non visibile ai clienti), ogni prodotto avrà anche associati un tempo di preparazione, una lista di ingredienti (con quantità) e opzionalmente la descrizione testuale della procedura di preparazione. Deve essere prevista anche la possibilità di scegliere tra differenti *caratteristiche* del prodotto, ognuna dotata di nome, descrizione (opzionale) e di differenza prezzo (rispetto al prezzo base del prodotto). Alcune caratteristiche potranno essere di *default* (quindi pre-selezionate). Infine,si potranno creare *gruppi di mutua esclusione* tra sottoinsiemi delle caratteristiche (in modo che solo una delle caratteristiche nel gruppo possa essere selezionata). *Esempio: il prodotto "caffè" potrebbe costare 1 euro, e avere come caratteristiche "senza zucchero" (-5 centesimi), "zuccherato" (default) e "molto zuccherato", raggruppate in un gruppo di mutua esclusione chiamato "zucchero", oltre a "con panna" (+50 centesimi) e "freddo" (+1 euro), liberamente selezionabili (non raggruppate).*

Le pagine pubbliche dei sito offriranno una panoramica sull'attività e sul menu, ma solo gli utenti registrati, o *clienti* potranno effettuare ordini. La registrazione sarà libera, ma si noti che i dati di un cliente dovranno necessariamente comprendere contatti telefonici e indirizzo, visto che parliamo di consegna a domicilio.

I clienti potranno selezionare uno o più prodotti dal menu, comprensivi di caratteristiche se presenti, creando un *ordine* a loro nome. Una volta composto l'ordine, il sistema fornirà al cliente una stima sul tempo di consegna dello stesso (che avrà come base la somma dei tempi di preparazione dei prodotti selezionati e potrebbe *opzionalmente* aggiungere una quantità di tempo proporzionale alla distanza tra l'attività e l'indirizzo dell'utente) e ovviamente il prezzo finale calcolato sommando quello di tutti i prodotti con le relative caratteristiche. L'utente potrà quindi confermare o annullare l'ordine. In caso di conferma, l'utente potrà anche selezionare un orario specifico per la consegna *(suggerimento: l'orario non potrà mai essere maggiore di quello di chiusura dell'attività o minore dell'ora attuale più il tempo di consegna stimato)*. Al termine del processo, il cliente riceverà una email riassuntiva sull'ordine effettuato, e ne riceverà una successiva quando l'ordine sarà "in consegna" (vedi dopo) *(suggerimento: potete provare a spedire realmente le email utilizzando un server SMTP di prova sulla vostra macchina, come [https://github.com/tntim96/FakeSMTP])*

L'applicazione avrà anche altre due tipologie di utenti: il *proprietario*, che supponiamo pre-caricato nel sistema all'atto della sua installazione, e il *personale*. Il primo potrà monitorare gli ordini passati, in preparazione ed evasi, potrà comporre il menu inserendo o modificando tutte le informazioni inerenti i prodotti e potrà infine registrare membri del personale.

I membri del personale vedranno la lista degli ordini correnti, che potranno avere cinque stati: *inserito*, *in preparazione*, *pronto*, *in consegna* e *consegnato*. Il personale potrà cambiare lo stato dell'ordine in qualsiasi momento, ma solo seguendo l'ordine progressivo (non si potrà riportare un ordine in consegna nello stato di preparazione). Ovviamente, il personale potrà vedere la scheda completa di ogni piatto inserito nell'ordine, comprese le informazioni non visibili al cliente. Il sistema dovrà tener traccia dell'effettivo membro del personale che effettua ciascun cambio di stato su un ordine.

*Grazie a Simone, Francesco e Stefano per aver ispirato questa specifica. Se è troppo complicata, prendetevela con loro* 😀


</section>


Di seguito sono illustrati schematicamente i contenuti e le
funzionalità minime che dovrebbero essere inseriti nel sito. Ovviamente, ogni
ulteriore raffinamento o arricchimento di queste specifiche aumenterà il valore
del progetto.

  <section class="operazioni">
  


- Gli utenti anonimi potranno visualizzare il menu (magari paginato, suddiviso per tipologia di prodotto... cercate di organizzarlo al meglio) e la descrizione dell'attività. L'accesso alle altre funzionalità sarà riservato solo ai clienti. 

- Gli utenti anonimi si potranno registrare direttamente sul sito diventando clienti. E' *opzionalmente* possibile dotare la procedura di registrazione di diversi tipi di verifica, ad esempio il controllo dell'indirizzo email tramite un link cliccabile.

- Scorrendo il menu, il cliente potrà comporre un ordine indicando per ogni prodotto le rispettive caratteristiche e aggiungendolo al carrello virtuale. Man mano che il carrello viene riempito, il cliente vedrà il prezzo totale della merce ordinata e il tempo stimato di consegna. 

- Al termine del processo di creazione dell'ordine, il cliente potrà confermarlo, selezionando l'ora preferita per la consegna.

- Il cliente potrà anche accedere a uno storico degli ordini già effettuati e seguire lo stato degli ordini confermati.

- Il proprietario potrà modificare liberamente i contenuti del menu.

- Il proprietario potrà visualizzare la lista degli ordini, suddivisa in base allo stato. In particolare, ogni ordine dovrà chiaramente mostrare la data/ora di consegna richiesta, lo stato corrente, il tempo di preparazione stimato, il numero di prodotti ordinati e il nome di tutti i membri del personale che ne hanno modificato lo stato (ad esempio *X ha posto l'ordine in preparazione, Y in preparato, Z in consegna*, ecc.).

- Il proprietario potrà accedere a delle viste che mostrano dati aggregati quali l'incasso giornaliero e mensile (selezionando un'opportuna data) e i prodotti più o meno ordinati. *Ogni altra statistica utile e non banale aggiunta al sistema aumenterà il valore del progetto.*

- I membri del personale disporranno di una vista simile a quella del proprietario, dalla quale sarà possibile in particolare modificare lo stato di un ordine, come già descritto.



  
  </section>

<section class="indicazioni break">

# Indicazioni per lo Sviluppo del Progetto

### Tecnologie da utilizzare

- La *struttura* base del sito deve essere realizzata in **HTML5**. La validazione
delle principali pagine del sito è parte integrante dello sviluppo e deve essere
riportata nella documentazione.

- Per la realizzazione del *layout* devono essere utilizzati il più
possibile i figli di stile CSS. Il layout può liberamente basarsi su quelli
disponibili in rete o utilizzati a lezione. Il grado di **personalizzazione**
del layout sarà comunque tenuto in considerazione in sede di valutazione. **Un
layout responsive non è strettamente richiesto ma fortemente consigliato.**

- Per la programmazione lato *client* il linguaggio richiesto
è JavaScript. Si possono liberamente includere nel progetto librerie sviluppate
da terze parti, a patto che la loro portabilità cross-browser sia adeguata e
che nella relazione siano citate e descritte. È in ogni caso **sconsigliato
l'abuso di tali tecnologie** , soprattutto quando sia possibile sostituirle
con un adeguato uso di HTML, CSS, ecc. In linea generale, è **ammissibile che gli
script giochino un ruolo più importante nelle funzionalità la cui utenza è
ristretta e predeterminata**, ad esempio nelle funzionalità *back-end*
per gli amministratori, ma non nel *front-end* pubblico del sito o in una
procedura di login. Su queste parti, invece, **l'uso del sito senza script potrebbe
essere meno "agevole" o permettere di accedere solo alle funzionalità "vitali"**. 

- Per la programmazione lato *server* è **richiesto** l'uso
di Java (*servlet*), eventualmente associato a qualsiasi *DBMS* (se necessario)
e a un *template engine* (come *Freemarker*). Anche in questo caso è
possibile avvalersi di librerie esterne.

- Il sito, in generale, deve funzionare ed avere un buon *rendering*
sulle versioni più recenti di Edge, Firefox e Chrome, e *possibilmente*
essere compatibile con i browser più datati (in questo caso non c'è bisogno che
tutto funzioni perfettamente, ma almeno che le funzionalità *degradino bene*)
e con le ultime versioni di altri browser, come Opera. Tale compatibilità **deve**
essere esplicitamente dichiarata nella documentazione.  

### Svolgimento e Documentazione del Progetto

Le specifiche fornite potrebbero non risultare esaustive o
completamente definite. Ogni funzionalità aggiunta o raffinata, anche tramite
l'interazione con il committente o con gli utenti finali del sito, sarà
adeguatamente valutata. Tutte le scelte progettuali vanno comunque discusse e
motivate.

Il progetto, svolto secondo le linee guida date dalle
specifiche, dovrà essere consegnato nella forma di un sito web completamente
funzionante, i cui contenuti e le cui caratteristiche saranno valutati in sede
d'esame. Le parti della specifica marcate come *opzionali*, se omesse, non
renderanno il progetto insufficiente ma non gli permetteranno comunque di
raggiungere il massimo dei voti. Nel caso si decida di realizzarle, non sarà
necessario che siano perfette o complete, ma che dimostrino chiaramente il
vostro impegno nell'affrontare una tematica avanzata.

La documentazione (**in formato elettronico**) che
accompagna il progetto **deve** contenere almeno le seguenti informazioni:

- Indicazione delle dipendenze software (di quali librerie avete
bisogno dal lato server e client?).

- Indicazione delle funzionalità realizzate e di quelle
eventualmente non realizzate. Descrizione dettagliata delle eventuali
funzionalità extra o opzionali inserite nel progetto.

- Diagramma che illustra la struttura e la navigabilità del sito (ad es.
un *navigation diagram*).

- Schema relazionale della base di dati (se presente).

- Descrizione analitica del layout del sito, con indicazione delle
sue principali componenti statiche/dinamiche.

- Descrizione delle eventuali tecnologie avanzate (linguaggi,
framework, plugin, librerie, ...) utilizzate, del motivo per cui sono state
adottate e del contributo effettivo che hanno dato alla realizzazione del
progetto.

- Descrizione di *eventuali* problemi riscontrati nella
fruizione del sito su browser differenti, lista dei browser compatibili.

- Screenshot delle pagine più importanti del sito (*opzionale*).

Nel caso di gruppi di lavoro composti da più componenti, *il
contributo effettivo offerto da ciascun componente* alla realizzazione
finale **deve** essere descritto nella documentazione (indicando, ad
esempio, chi si è dedicato prevalentemente alla programmazione server, chi ha
realizzato il layout, chi ha programmato lato client, ecc.). In sede di esame,
i responsabili potranno essere chiamati a riferire sugli aspetti loro delegati.  

### Consegna del Progetto

La *documentazione* del progetto, redatta come indicato nelle sezioni precedenti,
dovrà essere consegnata al docente **almeno due giorni prima** della data d'esame, 
inviandola semplicemente per email.   
*Non verranno in alcun caso concesse proroghe a questo termine*, perchè concedere 
un giorno in più a uno studente vorrebbe dire svantaggiare tutti gli altri che hanno 
consegnato per tempo o hanno organizzato i loro impegni per farlo, e che avrebbero 
anch'essi sicuramente beneficiato di un po' di tempo aggiuntivo, quantomeno per 
migliorare o rifinire il proprio lavoro.

*Non è necessario consegnare il codice*, che verrà analizzato e provato in sede d'esame
(portate quindi con voi una copia funzionante del progetto sul vostro PC portatile), 
ma è utile (se possibile) allegare alla documentazione la URL di un repository pubblico da 
cui questo può essere scaricato e ispezionato se necessario.


### Valutazione del Progetto

Nel valutare il progetto consegnato saranno prese in
considerazione le seguenti caratteristiche (in ordine di importanza):
1. Rispetto delle specifiche.
2. Correttezza tecnica.
3. Chiarezza e correttezza organizzativa dei contenuti.
4. Accessibilità e conformità agli standard.
5. Uso appropriato di contenuti statici e dinamici.
6. Qualità del design.
7. Adeguatezza della documentazione.

A questa valutazione si aggiungerà quella generale derivata
dalla discussione del progetto in sede d'esame.  

### Ulteriori Informazioni

Questa specifica è disponibile nel repository del corso di Web Engineering, 
all'indirizzo https://github.com/WebEngineering-Univaq/WE_Project_Specifications.
Ulteriori informazioni e chiarimenti sulle specifiche possono essere richiesti
direttamente via email all'indirizzo giuseppe.dellapenna@univaq.it.

Si ricorda che i progetti vanno svolti in *piccoli* gruppi (tre persone è il numero consigliato). 
Eccezioni a questa regola andranno concordate direttamente col docente.

An English translation of this project specification is also available in the course repository (https://github.com/WebEngineering-Univaq/WE_Project_Specifications).
If the translation is not there, ask the teacher to publish it :smile:
</section>

