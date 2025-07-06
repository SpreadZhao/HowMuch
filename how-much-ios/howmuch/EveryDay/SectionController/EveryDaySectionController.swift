//
//  EveryDaySectionController.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import UIKit
import IGListKit

final class EveryDaySectionController: ListBindingSectionController<ListDiffable>, ListBindingSectionControllerDataSource {
    
    override init() {
        super.init()
        self.dataSource = self
    }
    
    func sectionController(_ sectionController: ListBindingSectionController<any ListDiffable>, viewModelsFor object: Any) -> [any ListDiffable] {
        guard let object = object as? EveryDaySectionViewModel else {
            return []
        }
        var cellViewModels = [ListDiffable]()
        let date = object.date
        let transactions = object.transactions
        let everyDayHeaderCellViewModel = EveryDayHeaderCellViewModel(dateString: EveryDaySectionViewModel.dateFormatter.string(from: date))
        cellViewModels.append(everyDayHeaderCellViewModel)
        for transaction in transactions {
            let cellViewModel = EveryDayCellViewModel(id: transaction.id,
                                                      title: transaction.title ?? "",
                                                      description: transaction.description ?? "",
                                                      amount: transaction.amount)
            cellViewModels.append(cellViewModel)
        }
        return cellViewModels
    }
    
    func sectionController(_ sectionController: ListBindingSectionController<any ListDiffable>, cellForViewModel viewModel: Any, at index: Int) -> any UICollectionViewCell & ListBindable {
        if let viewModel = viewModel as? EveryDayHeaderCellViewModel {
            guard let cell = collectionContext.dequeueReusableCell(of: EveryDayHeaderCell.self, for: self, at: index) as? EveryDayHeaderCell else {
                return collectionContext.dequeueReusableCell(of: EveryDayEmptyCell.self, for: self, at: index) as! EveryDayEmptyCell
            }
            cell.bindViewModel(viewModel)
            return cell
        } else if let viewModel = viewModel as? EveryDayCellViewModel {
            guard let cell = collectionContext.dequeueReusableCell(of: EveryDayCell.self, for: self, at: index) as? EveryDayCell else {
                return collectionContext.dequeueReusableCell(of: EveryDayEmptyCell.self, for: self, at: index) as! EveryDayEmptyCell
            }
            cell.bindViewModel(viewModel)
            return cell
        }
        let cell = collectionContext.dequeueReusableCell(of: EveryDayEmptyCell.self, for: self, at: index) as! EveryDayEmptyCell
        return cell
    }
    
    func sectionController(_ sectionController: ListBindingSectionController<any ListDiffable>, sizeForViewModel viewModel: Any, at index: Int) -> CGSize {
        let width = collectionContext.containerSize.width
        let height = collectionContext.containerSize.height / 8
        return CGSize(width: width, height: height)
    }
    
}
