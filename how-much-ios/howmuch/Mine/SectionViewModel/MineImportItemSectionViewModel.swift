//
//  MineImportItemViewModel.swift
//  howmuch
//
//  Created by ljx on 2025/6/28.
//

import IGListKit

final class MineImportItemSectionViewModel: ListDiffable {
    
    let title: String
    
    init(title: String) {
        self.title = title
    }
    
    func diffIdentifier() -> any NSObjectProtocol {
        return title as NSString
    }
    
    func isEqual(toDiffableObject object: (any ListDiffable)?) -> Bool {
        guard let otherViewModel = object as? MineImportItemSectionViewModel else {
            return false
        }
        return title == otherViewModel.title
    }
    
}


