//
//  EveryDayHeaderViewModel.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import IGListKit

class EveryDayHeaderCellViewModel: ListDiffable {
    @Published var dateString: String
    
    init(dateString: String) {
        self.dateString = dateString
    }
    
    func diffIdentifier() -> any NSObjectProtocol {
        return dateString as NSString
    }
    
    func isEqual(toDiffableObject object: (any ListDiffable)?) -> Bool {
        guard let object = object as? EveryDayHeaderCellViewModel else {return false}
        return dateString == object.dateString
    }
    
}
