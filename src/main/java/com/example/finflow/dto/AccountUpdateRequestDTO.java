package com.example.finflow.dto;

import com.example.finflow.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateRequestDTO {
    private String currency;
    private AccountType type;
}
