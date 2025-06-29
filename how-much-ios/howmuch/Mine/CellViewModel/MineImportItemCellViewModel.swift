//
//  MineImportItemCellViewModel.swift
//  howmuch
//
//  Created by ljx on 2025/6/28.
//

import IGListKit

final class MineImportItemCellViewModel: ListDiffable {
    var title: String
    
    let iconName = "arrow.down.doc"
    
    init(title: String) {
        self.title = title
    }
    
    func diffIdentifier() -> any NSObjectProtocol {
        return title as NSString
    }
    
    func isEqual(toDiffableObject object: (any ListDiffable)?) -> Bool {
        guard let object = object as? MineImportItemCellViewModel else {
            return false
        }
        return title == object.title
    }
}
