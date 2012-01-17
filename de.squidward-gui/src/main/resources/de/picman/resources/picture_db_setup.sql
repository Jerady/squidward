--
-- TOC entry 1478 (class 1259 OID 16491)
-- Dependencies: 3
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE categories (
    id integer NOT NULL,
    name text NOT NULL,
    parentcategory integer
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- TOC entry 1479 (class 1259 OID 16497)
-- Dependencies: 1754 1755 3
-- Name: log; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE log (
    id integer NOT NULL,
    user_id integer,
    picture_id integer DEFAULT (-1),
    category_id integer DEFAULT (-1),
    message text NOT NULL,
    "timestamp" timestamp with time zone NOT NULL,
    level text NOT NULL
);


ALTER TABLE public.log OWNER TO postgres;

--
-- TOC entry 1480 (class 1259 OID 16505)
-- Dependencies: 1757 1758 3
-- Name: pictures; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pictures (
    id integer NOT NULL,
    description text,
    picture bytea,
    thumbnail bytea,
    preview bytea,
    saving_date timestamp with time zone NOT NULL,
    publication boolean DEFAULT false NOT NULL,
    origin text NOT NULL,
    exemplary boolean NOT NULL,
    bad_example boolean NOT NULL,
    creation_date text NOT NULL,
    user_id integer,
    deleted boolean DEFAULT false NOT NULL
);


ALTER TABLE public.pictures OWNER TO postgres;

--
-- TOC entry 1481 (class 1259 OID 16513)
-- Dependencies: 3
-- Name: pictures_to_categories; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pictures_to_categories (
    picture_id integer NOT NULL,
    category_id integer NOT NULL
);


ALTER TABLE public.pictures_to_categories OWNER TO postgres;

--
-- TOC entry 1482 (class 1259 OID 16516)
-- Dependencies: 1760 1762 3
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    name text NOT NULL,
    password text NOT NULL,
    fullname text DEFAULT 'unbekannt'::text,
    privileges integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 1483 (class 1259 OID 16524)
-- Dependencies: 1478 3
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE categories_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.categories_id_seq OWNER TO postgres;

--
-- TOC entry 1799 (class 0 OID 0)
-- Dependencies: 1483
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE categories_id_seq OWNED BY categories.id;


--
-- TOC entry 1800 (class 0 OID 0)
-- Dependencies: 1483
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('categories_id_seq', 105, true);


--
-- TOC entry 1484 (class 1259 OID 16526)
-- Dependencies: 1479 3
-- Name: log_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE log_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.log_id_seq OWNER TO postgres;

--
-- TOC entry 1801 (class 0 OID 0)
-- Dependencies: 1484
-- Name: log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE log_id_seq OWNED BY log.id;


--
-- TOC entry 1802 (class 0 OID 0)
-- Dependencies: 1484
-- Name: log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('log_id_seq', 22108, true);


--
-- TOC entry 1485 (class 1259 OID 16528)
-- Dependencies: 1480 3
-- Name: pictures_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE pictures_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.pictures_id_seq OWNER TO postgres;

--
-- TOC entry 1803 (class 0 OID 0)
-- Dependencies: 1485
-- Name: pictures_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE pictures_id_seq OWNED BY pictures.id;


--
-- TOC entry 1804 (class 0 OID 0)
-- Dependencies: 1485
-- Name: pictures_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('pictures_id_seq', 2015, true);


--
-- TOC entry 1486 (class 1259 OID 16530)
-- Dependencies: 3 1482
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE users_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 1805 (class 0 OID 0)
-- Dependencies: 1486
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 1806 (class 0 OID 0)
-- Dependencies: 1486
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('users_id_seq', 29, true);


--
-- TOC entry 1753 (class 2604 OID 16532)
-- Dependencies: 1483 1478
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE categories ALTER COLUMN id SET DEFAULT nextval('categories_id_seq'::regclass);


--
-- TOC entry 1756 (class 2604 OID 16533)
-- Dependencies: 1484 1479
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE log ALTER COLUMN id SET DEFAULT nextval('log_id_seq'::regclass);


--
-- TOC entry 1759 (class 2604 OID 16534)
-- Dependencies: 1485 1480
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE pictures ALTER COLUMN id SET DEFAULT nextval('pictures_id_seq'::regclass);


--
-- TOC entry 1761 (class 2604 OID 16535)
-- Dependencies: 1486 1482
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 1789 (class 0 OID 16491)
-- Dependencies: 1478
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO categories (id, name, parentcategory) VALUES (1, 'Landkreis', NULL);
INSERT INTO categories (id, name, parentcategory) VALUES (2, 'Gemeinde', NULL);
INSERT INTO categories (id, name, parentcategory) VALUES (3, 'Lebensräume', NULL);
INSERT INTO categories (id, name, parentcategory) VALUES (6, 'Sandacker', 4);
INSERT INTO categories (id, name, parentcategory) VALUES (5, 'Kalkscherbenacker', 4);
INSERT INTO categories (id, name, parentcategory) VALUES (4, 'Acker', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (7, 'Brache', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (8, 'Feldgehölz', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (9, 'Feuchtwiesen', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (10, 'Seggenriede', 9);
INSERT INTO categories (id, name, parentcategory) VALUES (11, 'Staudenflur', 9);
INSERT INTO categories (id, name, parentcategory) VALUES (12, 'Fließgewässer', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (13, 'Gipsabbau', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (14, 'Halbtrockenrasen', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (15, 'Hecken', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (16, 'Benjeshecke', 15);
INSERT INTO categories (id, name, parentcategory) VALUES (17, 'Heckensträucher', 15);
INSERT INTO categories (id, name, parentcategory) VALUES (18, 'Hutung', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (19, 'Kopfbäume
', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (20, 'Landschaften', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (21, 'Laubbäume', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (22, 'Moore', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (23, 'Sandlebensräume', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (24, 'Sandacker', 23);
INSERT INTO categories (id, name, parentcategory) VALUES (25, 'Sandgruben', 23);
INSERT INTO categories (id, name, parentcategory) VALUES (26, 'Sandmagerrasen', 23);
INSERT INTO categories (id, name, parentcategory) VALUES (27, 'Sonderstandorte', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (28, 'Höhle, Keller', 27);
INSERT INTO categories (id, name, parentcategory) VALUES (29, 'Lesesteine', 27);
INSERT INTO categories (id, name, parentcategory) VALUES (30, 'Mauern', 27);
INSERT INTO categories (id, name, parentcategory) VALUES (31, 'Steinbruch', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (32, 'Stillgewässer', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (33, 'Flachmulden', 32);
INSERT INTO categories (id, name, parentcategory) VALUES (34, 'Röhricht', 32);
INSERT INTO categories (id, name, parentcategory) VALUES (35, 'Teiche', 32);
INSERT INTO categories (id, name, parentcategory) VALUES (36, 'Verlandungsbereich', 32);
INSERT INTO categories (id, name, parentcategory) VALUES (37, 'Streuobst', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (38, 'Altbestand', 37);
INSERT INTO categories (id, name, parentcategory) VALUES (39, 'Einzelbaum', 37);
INSERT INTO categories (id, name, parentcategory) VALUES (40, 'Verbißschutz', 37);
INSERT INTO categories (id, name, parentcategory) VALUES (41, 'Tongrube', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (42, 'Totholz', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (43, 'Uferstreifen', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (44, 'Wald', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (45, 'Waldrand', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (46, 'Wirschaftswiesen', 3);
INSERT INTO categories (id, name, parentcategory) VALUES (47, 'Pflanze/Tier', NULL);
INSERT INTO categories (id, name, parentcategory) VALUES (48, 'Pflanze', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (49, 'Enzian', 48);
INSERT INTO categories (id, name, parentcategory) VALUES (50, 'Küchenschelle', 48);
INSERT INTO categories (id, name, parentcategory) VALUES (51, 'Orchidee', 48);
INSERT INTO categories (id, name, parentcategory) VALUES (52, 'Sonnentau', 48);
INSERT INTO categories (id, name, parentcategory) VALUES (53, 'Wollgras', 48);
INSERT INTO categories (id, name, parentcategory) VALUES (54, 'Amphibien', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (55, 'Moorfrosch', 54);
INSERT INTO categories (id, name, parentcategory) VALUES (56, 'Insekten', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (57, 'Krebse', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (58, 'Mollusken', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (59, 'Reptilien', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (60, 'Säugetiere', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (61, 'Spinnen', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (62, 'Vögel', 47);
INSERT INTO categories (id, name, parentcategory) VALUES (64, 'nur ein Test', NULL);
INSERT INTO categories (id, name, parentcategory) VALUES (65, 'nur ein Test', NULL);


--
-- TOC entry 1790 (class 0 OID 16497)
-- Dependencies: 1479
-- Data for Name: log; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 1791 (class 0 OID 16505)
-- Dependencies: 1480
-- Data for Name: pictures; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 1792 (class 0 OID 16513)
-- Dependencies: 1481
-- Data for Name: pictures_to_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 1793 (class 0 OID 16516)
-- Dependencies: 1482
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO users (id, name, password, fullname, privileges) VALUES (1, 'Ole', 'geheim', 'Ole Rahn', 0);


--
-- TOC entry 1765 (class 2606 OID 16558)
-- Dependencies: 1478 1478
-- Name: pk_categories_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY categories
    ADD CONSTRAINT pk_categories_id PRIMARY KEY (id);


--
-- TOC entry 1768 (class 2606 OID 16560)
-- Dependencies: 1479 1479
-- Name: pk_log; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY log
    ADD CONSTRAINT pk_log PRIMARY KEY (id);


--
-- TOC entry 1779 (class 2606 OID 16562)
-- Dependencies: 1481 1481 1481
-- Name: pk_picture_to_category; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY pictures_to_categories
    ADD CONSTRAINT pk_picture_to_category PRIMARY KEY (picture_id, category_id);


--
-- TOC entry 1777 (class 2606 OID 16564)
-- Dependencies: 1480 1480
-- Name: pk_pictures; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY pictures
    ADD CONSTRAINT pk_pictures PRIMARY KEY (id);


--
-- TOC entry 1781 (class 2606 OID 16566)
-- Dependencies: 1482 1482
-- Name: pk_users; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);


--
-- TOC entry 1783 (class 2606 OID 16568)
-- Dependencies: 1482 1482
-- Name: uc_users; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT uc_users UNIQUE (name);


--
-- TOC entry 1769 (class 1259 OID 16569)
-- Dependencies: 1480
-- Name: fki_pictures_user_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_pictures_user_id ON pictures USING btree (user_id);


--
-- TOC entry 1763 (class 1259 OID 16570)
-- Dependencies: 1478
-- Name: fki_pk_category_parent_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_pk_category_parent_id ON categories USING btree (parentcategory);


--
-- TOC entry 1766 (class 1259 OID 16571)
-- Dependencies: 1479
-- Name: ind_log_user; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ind_log_user ON log USING btree (user_id);


--
-- TOC entry 1770 (class 1259 OID 19152)
-- Dependencies: 1480
-- Name: index_pictures_bad_example; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_pictures_bad_example ON pictures USING btree (bad_example);


--
-- TOC entry 1771 (class 1259 OID 16572)
-- Dependencies: 1480
-- Name: index_pictures_deleted; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_pictures_deleted ON pictures USING btree (deleted);


--
-- TOC entry 1772 (class 1259 OID 19134)
-- Dependencies: 1480
-- Name: index_pictures_description; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_pictures_description ON pictures USING btree (description);


--
-- TOC entry 1773 (class 1259 OID 19147)
-- Dependencies: 1480
-- Name: index_pictures_exemplary; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_pictures_exemplary ON pictures USING btree (exemplary);


--
-- TOC entry 1774 (class 1259 OID 19148)
-- Dependencies: 1480
-- Name: index_pictures_publication; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_pictures_publication ON pictures USING btree (publication);


--
-- TOC entry 1775 (class 1259 OID 38010)
-- Dependencies: 1480
-- Name: index_pictures_saving_date; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX index_pictures_saving_date ON pictures USING btree (saving_date);


--
-- TOC entry 1787 (class 2606 OID 16573)
-- Dependencies: 1764 1478 1481
-- Name: fk_p2c_category_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pictures_to_categories
    ADD CONSTRAINT fk_p2c_category_id FOREIGN KEY (category_id) REFERENCES categories(id);


--
-- TOC entry 1788 (class 2606 OID 16578)
-- Dependencies: 1480 1776 1481
-- Name: fk_p2c_picture_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pictures_to_categories
    ADD CONSTRAINT fk_p2c_picture_id FOREIGN KEY (picture_id) REFERENCES pictures(id) ON DELETE CASCADE;


--
-- TOC entry 1786 (class 2606 OID 16583)
-- Dependencies: 1480 1482 1780
-- Name: fk_pictures_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pictures
    ADD CONSTRAINT fk_pictures_user_id FOREIGN KEY (user_id) REFERENCES users(id);


--
-- TOC entry 1784 (class 2606 OID 16588)
-- Dependencies: 1764 1478 1478
-- Name: pk_category_parent_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY categories
    ADD CONSTRAINT pk_category_parent_id FOREIGN KEY (parentcategory) REFERENCES categories(id) ON DELETE CASCADE;


--
-- TOC entry 1785 (class 2606 OID 16593)
-- Dependencies: 1482 1479 1780
-- Name: pk_log_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY log
    ADD CONSTRAINT pk_log_user_id FOREIGN KEY (user_id) REFERENCES users(id);


--
-- TOC entry 1798 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2009-01-17 17:30:39 CET

--
-- PostgreSQL database dump complete
--


