//
//  EveryDayViewController.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import UIKit
import IGListKit

final class EveryDayViewController: UIViewController {
    
    private lazy var collectionView = {
        return UICollectionView(frame: .zero, collectionViewLayout: UICollectionViewFlowLayout())
    }()
    
    private lazy var listAdapter = {
        return ListAdapter(updater: ListAdapterUpdater(), viewController: self, workingRangeSize: 0)
    }()
    
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        view.backgroundColor = .clear
        tabBarItem = UITabBarItem(title: "每日", image: UIImage(systemName: "book"), tag:TabBarItemType.mine.rawValue)
        listAdapter.collectionView = collectionView
        listAdapter.dataSource = self
        setupSubviews()
        setupLayouts()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setupSubviews() {
        view.addSubview(collectionView)
    }
    
    func setupLayouts() {
        collectionView.snp.makeConstraints { make in
            make.edges.equalTo(view)
        }
    }
    
}

extension EveryDayViewController: ListAdapterDataSource {
    func objects(for listAdapter: ListAdapter) -> [any ListDiffable] {
        var models = [ListDiffable]()
        var transactions = [TransactionModel]()
        let transaction0 = TransactionModel(id: UUID().uuidString, title: "lijiaxin", description: "123", amount: 1, date: .now, type: .income)
        let transaction1 = TransactionModel(id: UUID().uuidString, title: "liumeijing", description: "456", amount: 2, date: .now, type: .income)
        transactions.append(contentsOf: [transaction0, transaction1])
        let model = EveryDaySectionViewModel(date: .now, transactions: transactions)
        models.append(model)
        return models
    }
    
    func listAdapter(_ listAdapter: ListAdapter, sectionControllerFor object: Any) -> ListSectionController {
        return EveryDaySectionController()
    }
    
    func emptyView(for listAdapter: ListAdapter) -> UIView? {
        return nil
    }
}
