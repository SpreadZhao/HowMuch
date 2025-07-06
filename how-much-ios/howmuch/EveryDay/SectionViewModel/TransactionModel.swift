//
//  TransectionModel.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import Foundation

struct TransactionModel: Equatable {
    let id: String
    let title: String?
    let description: String?
    let amount: Double
    let date: Date
    let type: TransactionType
    
    init(id: String, title: String?, description: String?, amount: Double, date: Date, type: TransactionType) {
        self.id = id
        self.title = title
        self.description = description
        self.amount = amount
        self.date = date
        self.type = type
    }
    
    static func == (lhs: Self, rhs: Self) -> Bool {
        return lhs.id == rhs.id
    }
}
