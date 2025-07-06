//
//  EveryDayHeaderCell.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import IGListKit

final class EveryDayHeaderCell: UICollectionViewCell, ListBindable {
    
    private var viewModel: EveryDayHeaderCellViewModel?
    
    func bindViewModel(_ viewModel: Any) {
        guard let viewModel = viewModel as? EveryDayHeaderCellViewModel else {
            return
        }
        self.viewModel = viewModel
    }
    
}
