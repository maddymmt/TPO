create table Car
(
    ID       INTEGER not null
        constraint CAR_PK
            primary key,
    TYP      VARCHAR(255),
    NADWOZIE VARCHAR(255),
    MARKA    VARCHAR(255),
    CENA     INTEGER
)

