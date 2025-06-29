//
//  HMMineViewController.swift
//  howmuch
//
//  Created by ljx on 2025/6/28.
//

import UIKit
import IGListKit

final class MineViewController: UIViewController {
    
    private lazy var collectionView = {
        return UICollectionView(frame: .zero, collectionViewLayout: UICollectionViewFlowLayout())
    }()
    
    private lazy var models: [ListDiffable] = {
        var models = [ListDiffable]()
        models.append(MineSettingItemSectionViewModel(title: "设置"))
        models.append(MineImportItemSectionViewModel(title: "导入"))
        return models
    }()
    
    private lazy var listAdapter = ListAdapter(updater: ListAdapterUpdater(), viewController: self)
    
    // MARK: - Life Cycle
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        view.backgroundColor = .clear
        tabBarItem = UITabBarItem(title: "我的", image: UIImage(systemName: "person"), tag:TabBarItemType.mine.rawValue)
        listAdapter.collectionView = collectionView
        listAdapter.dataSource = self
        setupSubviews()
        setupLayouts()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
    }
    
    // MARK: - Private
    func setupSubviews() {
        view.addSubview(collectionView)
    }
    
    func setupLayouts() {
        collectionView.snp.makeConstraints { make in
            make.edges.equalTo(view)
        }
    }
    
}

extension MineViewController: ListAdapterDataSource {
    
    // MARK: - ListAdapterDataSource
    func objects(for listAdapter: ListAdapter) -> [any ListDiffable] {
        return models
    }
    
    func listAdapter(_ listAdapter: ListAdapter, sectionControllerFor object: Any) -> ListSectionController {
        switch object {
        case is MineSettingItemSectionViewModel:
            return MineSettingItemSectionController()
        case is MineImportItemSectionViewModel:
            return MineImportItemSectionController()
        default:
            return ListSectionController()
        }
    }
    
    func emptyView(for listAdapter: ListAdapter) -> UIView? {
        return nil
    }
    
}
