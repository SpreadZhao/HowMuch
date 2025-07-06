//
//  EverayDaySectionViewModel.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import IGListKit

final class EveryDaySectionViewModel: ListDiffable {
    
    let date: Date
    
    var transactions: [TransactionModel]
    
    init(date: Date, transactions: [TransactionModel]) {
        self.date = date
        self.transactions = transactions
    }
    
    func diffIdentifier() -> any NSObjectProtocol {
        return EveryDaySectionViewModel.dateFormatter.string(from: date) as NSString
    }
    
    func isEqual(toDiffableObject object: (any ListDiffable)?) -> Bool {
        guard let object = object as? EveryDaySectionViewModel else {
            return false
        }
        return dateStr == object.dateStr && transactions == object.transactions
    }
    
    var dateStr: String {
        return EveryDaySectionViewModel.dateFormatter.string(from: self.date)
    }
    
    // MARK: - Private
    static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()
    
}
