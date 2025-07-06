//
//  EveryDayCellViewModel.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import IGListKit

class EveryDayCellViewModel: ListDiffable {
    
    var id: String
    var title: String
    var description: String
    var amount: Double
    
    init(id: String, title: String, description: String, amount: Double) {
        self.id = id
        self.title = title
        self.description = description
        self.amount = amount
    }
    
    func diffIdentifier() -> any NSObjectProtocol {
        return id as NSString
    }
    
    func isEqual(toDiffableObject object: (any ListDiffable)?) -> Bool {
        guard let object = object as? EveryDayCellViewModel else {return false}
        return id == object.id
    }
    
}
