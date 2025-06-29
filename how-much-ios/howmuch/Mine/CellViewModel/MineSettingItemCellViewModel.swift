//
//  MineSettingItemCellViewModel.swift
//  howmuch
//
//  Created by ljx on 2025/6/28.
//

import IGListKit

final class MineSettingItemCellViewModel {
    let title: String
    
    let iconName = "gear"
    
    init(title: String) {
        self.title = title
    }
}

extension MineSettingItemCellViewModel: ListDiffable {
    
    func diffIdentifier() -> any NSObjectProtocol {
        return title as NSString
    }
    
    func isEqual(toDiffableObject object: (any ListDiffable)?) -> Bool {
        guard let other = object as? MineSettingItemCellViewModel else {
            return false
        }
        return title == other.title
    }
    
}
