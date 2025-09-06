-- No FKs since it contains historical data and referenced records could be already deleted
create table if not exists audit (
    id            uuid        not null default uuid_generate_v4(),
    initiator_id  uuid        not null,
    target_id     uuid,
    target_type   varchar(20) not null,
    action_type   varchar(20) not null,
    action_result varchar(10) not null,
    created_at    timestamp   not null,
    span_id       varchar(50),
    trace_id      varchar(50),
    details       jsonb,
    constraint pk_audit_id primary key (id)
);

create index idx_audit_initiator_id on audit (initiator_id);
create index idx_audit_target_id_target_type on audit (target_id, target_type);
create index idx_audit_created_at on audit (created_at);