package com.example.finflow.dto;

import com.example.finflow.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {
    private AccountType accountType;
    private String currency;
    private Long userId;
}
