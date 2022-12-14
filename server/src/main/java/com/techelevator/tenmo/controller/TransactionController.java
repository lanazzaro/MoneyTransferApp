package com.techelevator.tenmo.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransactionController {
    /*
    -send to spc user (cant be self, more than $0) --> update balance
    -request from spc user (cant be self, more than $0)
    -get transaction details by id
    -get transaction by user
    -get List of users --> put in user controller?
    -get List(??) of pending transactions
    -make pending transaction decision (checks)
        -if accept, update balance
        -change status
        -must have enough $
    -view transaction history
     */
}
