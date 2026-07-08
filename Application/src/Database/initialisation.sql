 
DROP DATABASE IF EXISTS projetweb;
CREATE DATABASE projetweb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE projetweb;
 
CREATE TABLE usine (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(150) NOT NULL,
    namespace_k8s   VARCHAR(100) UNIQUE,         
    actif           BOOLEAN DEFAULT TRUE,
    date_creation   DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
 
CREATE TABLE user (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usine_id        INT NOT NULL,
    username        VARCHAR(80) NOT NULL,
    password        VARCHAR(255) NOT NULL,       
    nom             VARCHAR(100),
    contact         VARCHAR(50),
    email           VARCHAR(150) UNIQUE,
    is_admin        BOOLEAN DEFAULT FALSE,
    actif           BOOLEAN DEFAULT TRUE,
    date_creation   DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_usine FOREIGN KEY (usine_id) REFERENCES usine(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_usine UNIQUE (usine_id, username)
) ENGINE=InnoDB;

CREATE TABLE roles (
    id                          INT AUTO_INCREMENT PRIMARY KEY,
    user_id                     INT NOT NULL UNIQUE,
    peut_ajouter_commande       BOOLEAN DEFAULT FALSE,
    peut_modifier_commande      BOOLEAN DEFAULT FALSE,
    peut_annuler_commande       BOOLEAN DEFAULT FALSE,
    peut_ajouter_produit        BOOLEAN DEFAULT FALSE,
    peut_modifier_produit       BOOLEAN DEFAULT FALSE,
    peut_supprimer_produit      BOOLEAN DEFAULT FALSE,
    est_admin                   BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_roles_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB;
 
CREATE TABLE produit (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usine_id        INT NOT NULL,
    nom             VARCHAR(150) NOT NULL,
    quantite        INT DEFAULT 0,
    prix_unitaire   DECIMAL(10,2) NOT NULL,
    description     TEXT,
    date_creation   DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_maj        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_produit_usine FOREIGN KEY (usine_id) REFERENCES usine(id) ON DELETE CASCADE
) ENGINE=InnoDB;
 
CREATE TABLE commande (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usine_id        INT NOT NULL,
    produit_id      INT NOT NULL,
    user_id         INT NOT NULL,                
    quantite        INT NOT NULL,
    prix_total      DECIMAL(10,2) NOT NULL,
    etat            ENUM('EN_ATTENTE','VALIDEE','EXPEDIEE','ANNULEE') DEFAULT 'EN_ATTENTE',
    date_creation   DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_maj        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_commande_usine   FOREIGN KEY (usine_id) REFERENCES usine(id) ON DELETE CASCADE,
    CONSTRAINT fk_commande_produit FOREIGN KEY (produit_id) REFERENCES produit(id),
    CONSTRAINT fk_commande_user    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;
 
CREATE TABLE historique (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usine_id        INT NOT NULL,
    user_id         INT,                        
    description     VARCHAR(500) NOT NULL,
    type_action     ENUM('USER','COMMANDE','STOCK','SECURITE','AUTRE') DEFAULT 'AUTRE',
    timestamp       DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hist_usine FOREIGN KEY (usine_id) REFERENCES usine(id) ON DELETE CASCADE,
    CONSTRAINT fk_hist_user  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE historique_commandes (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    historique_id   INT NOT NULL,
    commande_id     INT NOT NULL,
    etat_avant      VARCHAR(50),
    etat_apres      VARCHAR(50),
    CONSTRAINT fk_hc_hist     FOREIGN KEY (historique_id) REFERENCES historique(id) ON DELETE CASCADE,
    CONSTRAINT fk_hc_commande FOREIGN KEY (commande_id) REFERENCES commande(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE historique_stockage (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    historique_id   INT NOT NULL,
    produit_id      INT NOT NULL,
    quantite_avant  INT,
    quantite_apres  INT,
    CONSTRAINT fk_hs_hist    FOREIGN KEY (historique_id) REFERENCES historique(id) ON DELETE CASCADE,
    CONSTRAINT fk_hs_produit FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE CASCADE
) ENGINE=InnoDB;
 
CREATE TABLE ip_bloquee (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    adresse_ip      VARCHAR(45) NOT NULL UNIQUE,
    raison          VARCHAR(255),
    date_blocage    DATETIME DEFAULT CURRENT_TIMESTAMP,
    bloque_par      INT,
    CONSTRAINT fk_ip_user FOREIGN KEY (bloque_par) REFERENCES user(id)
) ENGINE=InnoDB;

CREATE TABLE intrusion (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    adresse_ip      VARCHAR(45) NOT NULL,
    description     VARCHAR(500),
    date_detection  DATETIME DEFAULT CURRENT_TIMESTAMP,
    traite          BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB;
 
CREATE INDEX idx_produit_usine   ON produit(usine_id);
CREATE INDEX idx_commande_usine  ON commande(usine_id);
CREATE INDEX idx_commande_etat   ON commande(etat);
CREATE INDEX idx_hist_usine_ts   ON historique(usine_id, timestamp);
 
INSERT INTO usine (nom, namespace_k8s) VALUES ('Usine Demo', 'usine-demo');
INSERT INTO user (usine_id, username, password, nom, contact, email, is_admin)
VALUES (1, 'admin', '$2a$10$ik0xEVRnr0FS1UHNLwvNuObWT1NzjRXWSGev8n5Bye0xgmAbFSbTu', 'Administrateur', '0600000000', 'admin@demo.local', TRUE);
INSERT INTO roles (user_id, peut_ajouter_commande, peut_modifier_commande, peut_annuler_commande,
                    peut_ajouter_produit, peut_modifier_produit, peut_supprimer_produit, est_admin)
VALUES (1, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE);
