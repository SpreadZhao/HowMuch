//
//  MineImportItemSectionController.swift
//  howmuch
//
//  Created by ljx on 2025/6/28.
//

import IGListKit

final class MineImportItemSectionController: ListBindingSectionController<ListDiffable>, ListBindingSectionControllerDataSource {
    
    override init() {
        super.init()
        self.dataSource = self
    }
    
    func sectionController(_ sectionController: ListBindingSectionController<any ListDiffable>, viewModelsFor object: Any) -> [any ListDiffable] {
        guard let sectionViewModel = object as? MineImportItemSectionViewModel else {
            return []
        }
        var viewModels = [ListDiffable]()
        let mineImportCellViewModel = MineImportItemCellViewModel(title: sectionViewModel.title)
        viewModels.append(mineImportCellViewModel)
        return viewModels
    }
    
    func sectionController(_ sectionController: ListBindingSectionController<any ListDiffable>, cellForViewModel viewModel: Any, at index: Int) -> any UICollectionViewCell & ListBindable {
        guard let viewModel = viewModel as? MineImportItemCellViewModel,
              let cell = collectionContext.dequeueReusableCell(of: MineImportItemCell.self, for: self, at: index) as? MineImportItemCell else {
            fatalError("Failed to dequeue MineSettingItemCell or viewModel类型不对")
        }
        cell.bindViewModel(viewModel)
        return cell
    }
    
    func sectionController(_ sectionController: ListBindingSectionController<any ListDiffable>, sizeForViewModel viewModel: Any, at index: Int) -> CGSize {
        let width = collectionContext.containerSize.width
        let height = collectionContext.containerSize.height / 8.0
        return CGSize(width: width, height: height)
    }
    
}
