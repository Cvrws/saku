package cc.unknown.command.impl;

import java.util.Collections;
import java.util.List;

import cc.unknown.command.Command;
import cc.unknown.handlers.TransactionHandler;

public final class Transaction extends Command {
    
    public Transaction() {
        super("Muestra las transacciones del servidor", "transaction");
    }
    
    @Override
    public void execute(final String[] args) {
    	TransactionHandler.start();
    }
    
    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return args.length == 1 ? Collections.singletonList("transaction") : Collections.emptyList();
    }
}